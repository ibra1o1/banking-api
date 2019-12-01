package banking.api.service;

import banking.api.dto.AccountDto;
import banking.data.model.Account;
import banking.data.repository.AccountRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Singleton
public class AccountService {

	@Inject
	private AccountRepository repository;

	private static AccountDto convert(final Account account) {
		return new AccountDto(account.getAccountHolder(), account.getAccountNumber(), account.getMoney().toString());
	}

	public AccountDto saveAccount(final AccountDto accountDto) {
		final Account account = repository.save(new Account(accountDto));
		return convert(account);
	}

	public AccountDto findBy(final String accountNumber) {
		final Account account = repository.findBy(accountNumber);
		return Objects.isNull(account) ? null : convert(account);
	}

	public List<AccountDto> findAll() {
		return repository.findAll().stream().map(AccountService::convert).collect(Collectors.toList());
	}


}
