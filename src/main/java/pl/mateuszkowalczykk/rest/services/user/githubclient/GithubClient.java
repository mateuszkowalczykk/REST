package pl.mateuszkowalczykk.rest.services.user.githubclient;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GithubClient {

  private static final String USERS_ENDPOINT = "users";

  private final String githubUrl;

  private final RestTemplate restTemplate;

  public GithubClient(@Value("${urls.github}") String githubUrl, RestTemplate restTemplate) {
    this.githubUrl = githubUrl;
    this.restTemplate = restTemplate;
  }

  public Optional<GithubUser> getGithubUserByLogin(String login) {
  //TODO Handling exceptions
    return Optional.ofNullable(restTemplate
        .getForObject(getUrl(USERS_ENDPOINT, login), GithubUser.class));
  }

  private String getUrl(String... pathElements) {
    final StringBuilder sb = new StringBuilder(githubUrl);

    for (String pathElement : pathElements) {
      sb.append("/").append(pathElement);
    }
    return sb.toString();
  }
}
