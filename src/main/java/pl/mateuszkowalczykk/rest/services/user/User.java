package pl.mateuszkowalczykk.rest.services.user;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {

  private Long id;

  private String login;

  private String name;

  private String type;

  private String avatarUrl;

  private LocalDateTime createdAt;

  private Double calculations;
}
