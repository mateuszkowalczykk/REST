package pl.mateuszkowalczykk.rest.services.user.githubclient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import pl.mateuszkowalczykk.rest.config.RestTemplateConfig;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(classes = {RestTemplateConfig.class, GithubClient.class})
class GithubClientTest {

  @Autowired private GithubClient githubClient;

  @Autowired private RestTemplate restTemplate;

  @Value("${urls.github}")
  private String githubUrl;

  private MockRestServiceServer mockGithubServer;

  @BeforeAll
  void setUp() {
    mockGithubServer = MockRestServiceServer.createServer(restTemplate);
  }

  @BeforeEach
  void reset() {
    mockGithubServer.reset();
  }

  @Test
  void shouldReturnGithubUserWithCorrectFieldValues() {
    // given
    this.mockGithubServer
        .expect(once(), requestTo(githubUrl + "/users/octocat"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(
            withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                    "{\"login\":\"octocat\",\"id\":583231,\"node_id\":\"MDQ6VXNlcjU4MzIzMQ==\",\"avatar_url\":\"https://avatars.githubusercontent.com/u/583231?v=4\",\"gravatar_id\":\"\",\"url\":\"https://api.github.com/users/octocat\",\"html_url\":\"https://github.com/octocat\",\"followers_url\":\"https://api.github.com/users/octocat/followers\",\"following_url\":\"https://api.github.com/users/octocat/following{/other_user}\",\"gists_url\":\"https://api.github.com/users/octocat/gists{/gist_id}\",\"starred_url\":\"https://api.github.com/users/octocat/starred{/owner}{/repo}\",\"subscriptions_url\":\"https://api.github.com/users/octocat/subscriptions\",\"organizations_url\":\"https://api.github.com/users/octocat/orgs\",\"repos_url\":\"https://api.github.com/users/octocat/repos\",\"events_url\":\"https://api.github.com/users/octocat/events{/privacy}\",\"received_events_url\":\"https://api.github.com/users/octocat/received_events\",\"type\":\"User\",\"site_admin\":false,\"name\":\"The Octocat\",\"company\":\"@github\",\"blog\":\"https://github.blog\",\"location\":\"San Francisco\",\"email\":null,\"hireable\":null,\"bio\":null,\"twitter_username\":null,\"public_repos\":8,\"public_gists\":8,\"followers\":5526,\"following\":9,\"created_at\":\"2011-01-25T18:44:36Z\",\"updated_at\":\"2022-03-22T14:07:15Z\"}"));

    // when
    Optional<GithubUser> result = githubClient.getGithubUserByLogin("octocat");

    // then
    assertThat(result)
        .isPresent()
        .get()
        .hasFieldOrPropertyWithValue("id", 583231L)
        .hasFieldOrPropertyWithValue("login", "octocat")
        .hasFieldOrPropertyWithValue("name", "The Octocat")
        .hasFieldOrPropertyWithValue(
            "avatarUrl", "https://avatars.githubusercontent.com/u/583231?v=4")
        .hasFieldOrPropertyWithValue("type", "User")
        .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.parse("2011-01-25T18:44:36"))
        .hasFieldOrPropertyWithValue("publicRepositories", 8)
        .hasFieldOrPropertyWithValue("followers", 5526);
  }

  @Test
  void shouldReturnEmptyOptional() {
    // given
    this.mockGithubServer
        .expect(once(), requestTo(githubUrl + "/users/nonExistentLogin"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(
            withStatus(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                    "{\"message\":\"Not Found\",\"documentation_url\":\"https://docs.github.com/rest/reference/users#get-a-user\"}"));

    // when
    Optional<GithubUser> result = githubClient.getGithubUserByLogin("nonExistentLogin");

    // then
    assertThat(result).isEmpty();
  }
}
