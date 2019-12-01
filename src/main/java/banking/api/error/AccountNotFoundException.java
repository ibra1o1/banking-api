package banking.api.error;

public class AccountNotFoundException extends RuntimeException {
  public AccountNotFoundException(String accountNumber) {
    super("Coudn't found an account With account_number: " + accountNumber);
  }
}
