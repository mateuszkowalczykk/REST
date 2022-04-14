package pl.mateuszkowalczykk.rest.domainevents.apievents;

import java.sql.SQLException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.jdbc.JdbcTestUtils;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest
@ActiveProfiles("h2")
class ApiEventStoreTest {

  @Autowired private JdbcTemplate jdbcTemplate;

  @Autowired private ApiEventStore apiEventStore;

  @BeforeAll
  void createTable() throws SQLException {
    ScriptUtils.executeSqlScript(
        jdbcTemplate.getDataSource().getConnection(),
        new ClassPathResource("db/migration/common/V2__create_users_api_log_table.sql"));
  }

  @BeforeEach
  void clearTable() {
    JdbcTestUtils.deleteFromTables(jdbcTemplate, "USERS_API_LOG");
  }

  @Test
  void shouldCreateRowInUsersApiLogTable() {
    // given
    String login = "login";

    // when
    apiEventStore.append(new GetUserByLoginApiEvent(login));

    // then
    SoftAssertions softly = new SoftAssertions();

    softly
        .assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "USERS_API_LOG"))
        .as("All table rows count")
        .isEqualTo(1);

    softly
        .assertThat(getRequestCountFromUserApiLog(login))
        .as(login + " request_count column")
        .isEqualTo(1);

    softly.assertAll();
  }

  @Test
  void shouldIncreaseRequestCountColumn() {
    // given
    String login = "login";

    // when
    apiEventStore.append(new GetUserByLoginApiEvent(login));
    apiEventStore.append(new GetUserByLoginApiEvent(login));

    // then
    SoftAssertions softly = new SoftAssertions();

    softly
        .assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "USERS_API_LOG"))
        .as("All table rows count")
        .isEqualTo(1);
    softly
        .assertThat(getRequestCountFromUserApiLog(login))
        .as(login + " request_count column")
        .isEqualTo(2);

    softly.assertAll();
  }

  @Test
  void shouldCreateRowsAndIncreaseRequestCount() {
    // given
    String login1 = "login1";
    String login2 = "login2";
    String login3 = "login3";

    // when
    apiEventStore.append(new GetUserByLoginApiEvent(login1));

    apiEventStore.append(new GetUserByLoginApiEvent(login2));
    apiEventStore.append(new GetUserByLoginApiEvent(login2));

    apiEventStore.append(new GetUserByLoginApiEvent(login3));
    apiEventStore.append(new GetUserByLoginApiEvent(login3));
    apiEventStore.append(new GetUserByLoginApiEvent(login3));

    // then
    SoftAssertions softly = new SoftAssertions();

    softly
        .assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "USERS_API_LOG"))
        .as("All table rows count")
        .isEqualTo(3);
    softly
        .assertThat(getRequestCountFromUserApiLog(login1))
        .as(login1 + " request_count column")
        .isEqualTo(1);
    softly
        .assertThat(getRequestCountFromUserApiLog(login2))
        .as(login2 + " request_count column")
        .isEqualTo(2);
    softly
        .assertThat(getRequestCountFromUserApiLog(login3))
        .as(login3 + " request_count column")
        .isEqualTo(3);

    softly.assertAll();
  }

  private int getRequestCountFromUserApiLog(String login) {
    try {
      return jdbcTemplate.queryForObject(
          "SELECT REQUEST_COUNT FROM USERS_API_LOG WHERE LOGIN = ?",
          (rs, rowNum) -> rs.getInt(1),
          login);
    } catch (EmptyResultDataAccessException e) {
      return 0;
    }
  }
}
