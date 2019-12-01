package banking.data.model;

import banking.api.dto.AccountDto;
import lombok.Getter;
import lombok.Setter;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

@Getter
public class Account {

	private final ReentrantLock lock = new ReentrantLock();
	private final String accountNumber = UUID.randomUUID().toString();
	private final CurrencyUnit currencyUnit;
	@Setter
	private String accountHolder;
	@Setter
	private Money money;

	public Account(final AccountDto accountDto) {
		this.accountHolder = accountDto.getAccountHolder();
		this.money = Money.parse(accountDto.getCurrencyAndBalance());
		this.currencyUnit = money.getCurrencyUnit();
	}

	public void lock() {
		this.lock.lock();
	}

	public void unlock() {
		this.lock.unlock();
	}


}
