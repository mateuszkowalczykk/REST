package pl.mateuszkowalczykk.rest.domainevents.apievents;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.only;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ApiEventListenerTest {

  @InjectMocks private ApiEventListener apiEventListener;

  @Mock private ApiEventStore apiEventStore;

  @Test
  void shouldHandleGetUserByLoginApiEvent() {
    // given
    GetUserByLoginApiEvent event = new GetUserByLoginApiEvent("dummyLogin");

    // when
    apiEventListener.handle(event);

    // then
    then(apiEventStore).should(only()).append(event);
  }
}
