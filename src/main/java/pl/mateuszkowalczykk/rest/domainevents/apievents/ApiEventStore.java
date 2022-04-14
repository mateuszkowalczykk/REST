package pl.mateuszkowalczykk.rest.domainevents.apievents;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApiEventStore {

  private final JdbcTemplate jdbcTemplate;

  public void append(GetUserByLoginApiEvent event) {
    jdbcTemplate.execute(
        String.format(
            "INSERT INTO USERS_API_LOG (LOGIN) VALUES ('%s') "
                + "ON DUPLICATE KEY UPDATE REQUEST_COUNT = REQUEST_COUNT + 1",
            event.getLogin()));
  }
}
