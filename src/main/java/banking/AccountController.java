package banking;

import banking.api.dto.AccountDto;
import banking.api.service.AccountService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.RequestAttribute;

import javax.inject.Inject;
import java.util.List;

/**
 * This controller only for test purpose, to be able to create accounts as we like.
 */
@Controller("/accounts")
public class AccountController {

  @Inject
  private AccountService service;

  @Post("/create")
  public AccountDto create(final AccountDto accountDto) {
    return service.saveAccount(accountDto);
  }

  @Get("/list")
  public List<AccountDto> getAll() {
    return service.findAll();
  }

  @Get("/{accountNumber}")
  public AccountDto getByAccountNumber(@RequestAttribute String accountNumber) {
    return service.findBy(accountNumber);
  }


}
