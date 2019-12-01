package banking;

import banking.api.dto.AccountDto;
import banking.api.dto.TransferDto;
import banking.api.dto.TransferResponseDto;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;
import org.joda.money.Money;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest(packages = "banking")
public class PaymentControllerTest {

	@Inject
	@Client("/")
	RxHttpClient client;

	@Test
	public void shouldTransferMoneyWithSameCurrency() {
		//given:
		AccountDto firstAccount = new AccountDto("Ibra", "", "EUR 20.5");
		AccountDto secondAccount = new AccountDto("Marc", "", "EUR 5");
		AccountDto first = client.toBlocking().retrieve(HttpRequest.POST("/accounts/create", firstAccount), AccountDto.class);
		AccountDto second = client.toBlocking().retrieve(HttpRequest.POST("/accounts/create", secondAccount), AccountDto.class);
		TransferDto transferDto = new TransferDto(first.getAccountNumber(), second.getAccountNumber(), "EUR 2");

		//when:
		TransferResponseDto response = client.toBlocking().retrieve(HttpRequest.POST("/payments/send", transferDto), TransferResponseDto.class);
		AccountDto firstAccountRes = client.toBlocking().retrieve(HttpRequest.GET("/accounts/" + first.getAccountNumber()), AccountDto.class);
		AccountDto secondAccountRes = client.toBlocking().retrieve(HttpRequest.GET("/accounts/" + second.getAccountNumber()), AccountDto.class);

		//then:
		assertEquals("EUR 18.50", response.getCurrentBalance());
		assertEquals(firstAccountRes.getCurrencyAndBalance(), "EUR 18.50");
		assertEquals(secondAccountRes.getCurrencyAndBalance(), "EUR 7.00");
	}

	@Test
	public void shouldTransferMoneyWithDifferentCurrency() {
		//given:
		AccountDto firstAccount = new AccountDto("Ibra", "", "USD 20.5");
		AccountDto secondAccount = new AccountDto("Marc", "", "EUR 5");
		AccountDto first = client.toBlocking().retrieve(HttpRequest.POST("/accounts/create", firstAccount), AccountDto.class);
		AccountDto second = client.toBlocking().retrieve(HttpRequest.POST("/accounts/create", secondAccount), AccountDto.class);
		TransferDto transferDto = new TransferDto(first.getAccountNumber(), second.getAccountNumber(), "EUR 2");

		//when:
		TransferResponseDto response = client.toBlocking().retrieve(HttpRequest.POST("/payments/send", transferDto), TransferResponseDto.class);
		AccountDto firstAccountRes = client.toBlocking().retrieve(HttpRequest.GET("/accounts/" + first.getAccountNumber()), AccountDto.class);
		AccountDto secondAccountRes = client.toBlocking().retrieve(HttpRequest.GET("/accounts/" + second.getAccountNumber()), AccountDto.class);

		//then:
		assertEquals("USD 18.29", response.getCurrentBalance());
		assertEquals(firstAccountRes.getCurrencyAndBalance(), "USD 18.29");
		assertEquals(secondAccountRes.getCurrencyAndBalance(), "EUR 7.00");
	}

	@Test
	public void shouldNotTransferMoneyIfNoSufficientFund() {
		//given:
		AccountDto firstAccount = new AccountDto("Ibra", "", "EUR 2");
		AccountDto secondAccount = new AccountDto("Marc", "", "EUR 5");
		AccountDto first = client.toBlocking().retrieve(HttpRequest.POST("/accounts/create", firstAccount), AccountDto.class);
		AccountDto second = client.toBlocking().retrieve(HttpRequest.POST("/accounts/create", secondAccount), AccountDto.class);
		TransferDto transferDto = new TransferDto(first.getAccountNumber(), second.getAccountNumber(), "EUR 3");

		//when:
		check(transferDto, HttpStatus.BAD_REQUEST);
		AccountDto firstAccountRes = client.toBlocking().retrieve(HttpRequest.GET("/accounts/" + first.getAccountNumber()), AccountDto.class);
		AccountDto secondAccountRes = client.toBlocking().retrieve(HttpRequest.GET("/accounts/" + second.getAccountNumber()), AccountDto.class);

		//then:
		assertEquals(firstAccountRes.getCurrencyAndBalance(), "EUR 2.00");
		assertEquals(secondAccountRes.getCurrencyAndBalance(), "EUR 5.00");
	}


	@Test
	public void shouldHandleConcurrencyAndTransferTheMoney() throws ExecutionException, InterruptedException {
		//given:
		AccountDto firstAccount = new AccountDto("Ibra", "", "EUR 20.5");
		AccountDto secondAccount = new AccountDto("Marc", "", "EUR 5");
		AccountDto thirdAccount = new AccountDto("Chris", "", "EUR 1");
		AccountDto first = client.toBlocking().retrieve(HttpRequest.POST("/accounts/create", firstAccount), AccountDto.class);
		AccountDto second = client.toBlocking().retrieve(HttpRequest.POST("/accounts/create", secondAccount), AccountDto.class);
		AccountDto third = client.toBlocking().retrieve(HttpRequest.POST("/accounts/create", thirdAccount), AccountDto.class);
		TransferDto transferDto = new TransferDto(first.getAccountNumber(), second.getAccountNumber(), "EUR 2");
		TransferDto secondTransferDto = new TransferDto(first.getAccountNumber(), third.getAccountNumber(), "EUR 10");

		//when:
		CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> client.toBlocking().retrieve(HttpRequest.POST("/payments/send", transferDto), TransferResponseDto.class));
		CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> client.toBlocking().retrieve(HttpRequest.POST("/payments/send", secondTransferDto), TransferResponseDto.class));

		CompletableFuture<Void> future = CompletableFuture.allOf(future1, future2);

		future.get();
		AccountDto firstAccountRes = client.toBlocking().retrieve(HttpRequest.GET("/accounts/" + first.getAccountNumber()), AccountDto.class);
		AccountDto secondAccountRes = client.toBlocking().retrieve(HttpRequest.GET("/accounts/" + second.getAccountNumber()), AccountDto.class);
		AccountDto thirdAccountRes = client.toBlocking().retrieve(HttpRequest.GET("/accounts/" + third.getAccountNumber()), AccountDto.class);
		//then:
		assertEquals(firstAccountRes.getCurrencyAndBalance(), "EUR 8.50");
		assertEquals(secondAccountRes.getCurrencyAndBalance(), "EUR 7.00");
		assertEquals(thirdAccountRes.getCurrencyAndBalance(), "EUR 11.00");
	}

	@Test
	public void moneyShouldHasImmutableBehaviour() throws ExecutionException, InterruptedException {
		//given:
		AccountDto firstAccount = new AccountDto("Ibra", "", "EUR 20.5");
		AccountDto secondAccount = new AccountDto("Marc", "", "EUR 5");
		AccountDto thirdAccount = new AccountDto("Chris", "", "EUR 1");
		AccountDto first = client.toBlocking().retrieve(HttpRequest.POST("/accounts/create", firstAccount), AccountDto.class);
		AccountDto second = client.toBlocking().retrieve(HttpRequest.POST("/accounts/create", secondAccount), AccountDto.class);
		AccountDto third = client.toBlocking().retrieve(HttpRequest.POST("/accounts/create", thirdAccount), AccountDto.class);
		TransferDto transferDto = new TransferDto(first.getAccountNumber(), second.getAccountNumber(), "EUR 20");
		TransferDto secondTransferDto = new TransferDto(first.getAccountNumber(), third.getAccountNumber(), "EUR 10");

		//when:
		CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> check(transferDto, HttpStatus.BAD_REQUEST));
		CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> check(secondTransferDto, HttpStatus.BAD_REQUEST));

		CompletableFuture<Void> future = CompletableFuture.allOf(future1, future2);

		future.get();
		AccountDto firstAccountRes = client.toBlocking().retrieve(HttpRequest.GET("/accounts/" + first.getAccountNumber()), AccountDto.class);
		AccountDto secondAccountRes = client.toBlocking().retrieve(HttpRequest.GET("/accounts/" + second.getAccountNumber()), AccountDto.class);
		AccountDto thirdAccountRes = client.toBlocking().retrieve(HttpRequest.GET("/accounts/" + third.getAccountNumber()), AccountDto.class);
		Money total = Money.parse(firstAccountRes.getCurrencyAndBalance()).plus(Money.parse(secondAccountRes.getCurrencyAndBalance())).plus(Money.parse(thirdAccountRes.getCurrencyAndBalance()));

		//then:
		assertEquals("EUR 26.50", total.toString());
	}


	private void check(TransferDto transferDto, HttpStatus status) {
		try {
			client.toBlocking().retrieve(HttpRequest.POST("/payments/send", transferDto), TransferResponseDto.class);
		} catch (HttpClientResponseException e) {
			assertEquals(e.getStatus(), status);
		}
	}

}
