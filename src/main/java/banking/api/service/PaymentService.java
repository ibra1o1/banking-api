package banking.api.service;

import banking.api.dto.TransferDto;
import banking.api.dto.TransferResponseDto;
import banking.api.error.AccountNotFoundException;
import banking.api.error.InsufficientFundException;
import banking.api.error.TransactionException;
import banking.data.model.Account;
import banking.data.repository.AccountRepository;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import static banking.api.service.ConvertionRateService.conversionRate;


@Singleton
public class PaymentService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);
	@Inject
	private AccountRepository repository;


	public TransferResponseDto sendMoney(final TransferDto transferDto) {

		final Account sender = repository.findBy(transferDto.getSenderAccountNumber());
		final Account beneficiary = repository.findBy(transferDto.getBeneficiaryAccountNumber());
		validateAccounts(transferDto, sender, beneficiary);
		return sendInTransaction(transferDto, sender, beneficiary);
	}

	private TransferResponseDto sendInTransaction(final TransferDto transferDto, final Account sender, final Account beneficiary) {
		sender.lock();
		beneficiary.lock();
		final Money senderMoney = Money.parse(sender.getMoney().toString());
		final Money beneficiaryMoney = Money.parse(beneficiary.getMoney().toString());
		try {

			final Money transferAmount = Money.parse(transferDto.getCurrencyAndAmount());
			final Money amountFromSender = convertTo(sender.getMoney().getCurrencyUnit(), transferAmount);

			checkFund(sender, amountFromSender);

			repository.updateBalance(sender, sender.getMoney().minus(amountFromSender));
			final Money amountToBeneficiary = convertTo(beneficiary.getMoney().getCurrencyUnit(), transferAmount);
			repository.updateBalance(beneficiary, beneficiary.getMoney().plus(amountToBeneficiary));

		} catch (Exception e) {

			if (e instanceof InsufficientFundException) {
				throw e;
			}
			//RollBack
			LOGGER.error("Could'nt complete the transaction.", e);
			repository.updateBalance(sender, senderMoney);
			repository.updateBalance(beneficiary, beneficiaryMoney);
			throw new TransactionException(sender.getMoney().toString());
		} finally {
			sender.unlock();
			beneficiary.unlock();
		}
		LOGGER.info("Transaction Completed {}", transferDto.toString());
		return new TransferResponseDto("Transaction Done Successfully!", sender.getMoney().toString(), LocalDateTime.now().toString());
	}

	private void checkFund(final Account sender, final Money amountFromSender) {
		if (sender.getMoney().isLessThan(amountFromSender)) {
			LOGGER.error("Couldn't complete the transaction due to insufficient money for user {}", sender.getAccountHolder());
			throw new InsufficientFundException(sender.getMoney().toString());
		}
	}

	private void validateAccounts(TransferDto transferDto, Account sender, Account beneficiary) {
		if (sender == null) {
			LOGGER.error("Couldn't found account {}", transferDto.getSenderAccountNumber());
			throw new AccountNotFoundException(transferDto.getSenderAccountNumber());
		}
		if (beneficiary == null) {
			LOGGER.error("Couldn't found account {}", transferDto.getBeneficiaryAccountNumber());
			throw new AccountNotFoundException(transferDto.getBeneficiaryAccountNumber());
		}
	}

	private Money convertTo(final CurrencyUnit currencyUnit, final Money money) {
		final BigDecimal rate = conversionRate(money.getCurrencyUnit().getCode(), currencyUnit.getCode());
		return money.convertedTo(currencyUnit, rate, RoundingMode.UP);
	}
}
