package banking.api.error;

public class InsufficientFundException extends RuntimeException {
  public InsufficientFundException(String currentBalance) {
    super(String.format("You have insufficient fund, current_balance: " + currentBalance));
  }
}
