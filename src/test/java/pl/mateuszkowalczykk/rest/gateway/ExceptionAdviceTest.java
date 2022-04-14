package pl.mateuszkowalczykk.rest.gateway;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import java.time.LocalDateTime;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import pl.mateuszkowalczykk.rest.gateway.ExceptionAdvice.ErrorResponseBody;

class ExceptionAdviceTest {

  private final ExceptionAdvice exceptionAdvice = new ExceptionAdvice();
  private static MockedStatic<LocalDateTime> currentTimestampMock;

  @BeforeAll
  static void setup() {
    currentTimestampMock =
        mockStatic(
            LocalDateTime.class,
            invocation -> {
              if (invocation.getMethod().getName().equals("now")) {
                return LocalDateTime.parse("2022-04-13T14:49:25.000000123");
              }
              return invocation.callRealMethod();
            });
  }

  @AfterAll
  static void tearDown() {
    currentTimestampMock.close();
  }

  @Test
  void shouldReturnCorrectResponseEntityForUnknownException() {
    // given
    WebRequest requestMock = mock(WebRequest.class);
    String requestDetails = "uri=/dummy/endpoint";

    given(requestMock.getDescription(anyBoolean())).willReturn(requestDetails);

    // when
    ResponseEntity<ErrorResponseBody> result =
        exceptionAdvice.handleUnknownExceptions(
            new RuntimeException("Dummy exception message"), requestMock);

    // then
    then(requestMock).should(times(2)).getDescription(anyBoolean());

    SoftAssertions softly = new SoftAssertions();

    softly
        .assertThat(result.getStatusCode())
        .as("Status code")
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

    softly
        .assertThat(result.getBody())
        .as("Response body")
        .hasFieldOrPropertyWithValue(
            "timestamp", LocalDateTime.parse("2022-04-13T14:49:25.000000123"))
        .hasFieldOrPropertyWithValue("status", 500)
        .hasFieldOrPropertyWithValue("type", "Internal Server Error")
        .hasFieldOrPropertyWithValue("message", "")
        .hasFieldOrPropertyWithValue("requestDetails", requestDetails);

    softly.assertAll();
  }
}
