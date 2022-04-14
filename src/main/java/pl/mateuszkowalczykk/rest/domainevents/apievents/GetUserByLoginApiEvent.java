package pl.mateuszkowalczykk.rest.domainevents.apievents;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.mateuszkowalczykk.rest.domainevents.DomainEvent;

@Getter
@RequiredArgsConstructor
public class GetUserByLoginApiEvent extends DomainEvent {

  private final String login;
}
