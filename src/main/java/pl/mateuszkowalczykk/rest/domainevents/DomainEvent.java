package pl.mateuszkowalczykk.rest.domainevents;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

@Getter
public abstract class DomainEvent {

  private final UUID id = UUID.randomUUID();

  private final LocalDateTime timestamp = LocalDateTime.now();
}
