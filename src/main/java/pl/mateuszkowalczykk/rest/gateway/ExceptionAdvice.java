package pl.mateuszkowalczykk.rest.gateway;

import java.time.LocalDateTime;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import pl.mateuszkowalczykk.rest.services.user.UserNotFoundException;


@RestControllerAdvice
public class ExceptionAdvice {

  private final Logger log = LoggerFactory.getLogger(ExceptionAdvice.class);

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponseBody> handleUserNotFoundException(Exception e, WebRequest request) {
    return createErrorResponse(HttpStatus.NOT_FOUND, request, e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseBody> handleUnknownExceptions(Exception e, WebRequest request) {
    log.error(
        "Error for Api request: '{}'. Error: {}.",
        request.getDescription(true),
        e.toString()
    );
    return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, request);
  }


  private ResponseEntity<ErrorResponseBody> createErrorResponse(HttpStatus httpStatus, WebRequest webRequest) {
    return createErrorResponse(httpStatus, webRequest, null);
  }

  private ResponseEntity<ErrorResponseBody> createErrorResponse(HttpStatus httpStatus, WebRequest webRequest,
      String message) {

    return ResponseEntity.status(httpStatus)
        .body(new ErrorResponseBody(httpStatus, webRequest, message));
  }


  @Getter
  static class ErrorResponseBody {

    private final LocalDateTime timestamp;
    private final int status;
    private final String type;
    private final String message;
    private final String requestDetails;

    public ErrorResponseBody(HttpStatus httpStatus,
        WebRequest webRequest, String message) {
      this.timestamp = LocalDateTime.now();
      this.status = httpStatus.value();
      this.type = httpStatus.getReasonPhrase();
      this.message = message == null ?
          ""
          : message;
      this.requestDetails = webRequest.getDescription(false);
    }
  }
}
