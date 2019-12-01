package banking.data.repository;

import banking.data.error.IncompatibleCurrency;
import banking.data.model.Account;
import org.joda.money.Money;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class AccountRepository {
	private final Map<String, Account> accountStorage = new ConcurrentHashMap<>();

	public Account save(final Account account) {
		accountStorage.put(account.getAccountNumber(), account);
		return findBy(account.getAccountNumber());
	}

	public Account findBy(final String accountNumber) {
		return accountStorage.get(accountNumber);
	}

	public List<Account> findAll() {
		return new ArrayList<>(accountStorage.values());
	}

	public void updateBalance(final Account account, final Money money) {
		if (!account.getCurrencyUnit().equals(money.getCurrencyUnit())) {
			throw new IncompatibleCurrency();
		}
		account.setMoney(money);
	}
}
