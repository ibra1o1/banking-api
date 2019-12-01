package banking.api.error;

public class TransactionException extends RuntimeException {
	public TransactionException(String currentBalance) {
		super("Couldn't Complete Transaction, current_balance: " + currentBalance);
	}
}
