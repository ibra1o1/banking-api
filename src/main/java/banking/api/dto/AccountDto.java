package banking.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
  @JsonProperty("account_holder")
  private String accountHolder;
  @JsonProperty("account_number")
  private String accountNumber;
  @JsonProperty("currency_and_balance")
  private String currencyAndBalance; // e.g "EUR 28.15"
}
