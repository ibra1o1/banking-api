package banking.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferDto {
	@JsonProperty("sender_account_number")
	private String senderAccountNumber;
	@JsonProperty("beneficiary_account_number")
	private String beneficiaryAccountNumber;
	@JsonProperty("currency_and_amount")
	private String currencyAndAmount; //e.g "USD 20.12"
}
