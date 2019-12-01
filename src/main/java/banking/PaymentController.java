package banking;

import banking.api.dto.TransferDto;
import banking.api.dto.TransferResponseDto;
import banking.api.service.PaymentService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;

import javax.inject.Inject;

@Controller("/payments")
public class PaymentController {

  @Inject
  private PaymentService service;

  @Post("/send")
  public TransferResponseDto sendMoney(final TransferDto transferDto) {
    return service.sendMoney(transferDto);
  }
}
