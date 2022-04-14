package pl.mateuszkowalczykk.rest.domainevents.apievents;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApiEventListener {

  private final ApiEventStore apiEventStore;

  @EventListener
  public void handle(GetUserByLoginApiEvent getUserByLoginApiEvent) {
    apiEventStore.append(getUserByLoginApiEvent);
  }
}
