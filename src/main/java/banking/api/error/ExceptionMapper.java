package banking.api.error;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;

@Controller
public class ExceptionMapper {

  @Error(global = true)
  public HttpResponse<JsonError> internalError(HttpRequest request, TransactionException e) {
    JsonError error = new JsonError(e.getMessage())
            .link(Link.SELF, Link.of(request.getUri()));

    return HttpResponse.<JsonError>status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(error);
  }

  @Error(global = true)
  public HttpResponse<JsonError> accountError(HttpRequest request, AccountNotFoundException e) {
    JsonError error = new JsonError(e.getMessage())
            .link(Link.SELF, Link.of(request.getUri()));

    return HttpResponse.<JsonError>status(HttpStatus.NOT_FOUND)
            .body(error);
  }

  @Error(global = true)
  public HttpResponse<JsonError> fundError(HttpRequest request, InsufficientFundException e) {
    JsonError error = new JsonError(e.getMessage())
            .link(Link.SELF, Link.of(request.getUri()));

    return HttpResponse.<JsonError>status(HttpStatus.BAD_REQUEST)
            .body(error);
  }

}
