package pl.mateuszkowalczykk.rest.domainevents;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.only;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import pl.mateuszkowalczykk.rest.domainevents.apievents.GetUserByLoginApiEvent;

@ExtendWith(MockitoExtension.class)
class DomainEventPublisherTest {

  @InjectMocks private DomainEventPublisher domainEventPublisher;

  @Mock private ApplicationEventPublisher applicationEventPublisher;

  @Test
  void shouldPublishEventExtendsDomainEvent() {
    // given
    GetUserByLoginApiEvent event = new GetUserByLoginApiEvent("dummyLogin");

    // when
    domainEventPublisher.publish(event);

    // then
    then(applicationEventPublisher).should(only()).publishEvent(event);
  }
}
