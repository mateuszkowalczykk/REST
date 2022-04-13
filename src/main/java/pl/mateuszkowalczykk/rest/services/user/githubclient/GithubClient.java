package pl.mateuszkowalczykk.rest.services.user.githubclient;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.client.RestClientException;
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
    try {
      return Optional.ofNullable(
          restTemplate
              .getForObject(
                  createUrl(USERS_ENDPOINT, login),
                  GithubUser.class
              )
      );
    } catch (NotFound e) {
      return Optional.empty();
    } catch (RestClientException e) {
      throw new GithubClientException(e);
    }
  }

  private String createUrl(String... pathElements) {
    final StringBuilder sb = new StringBuilder(githubUrl);

    for (String pathElement : pathElements) {
      sb.append("/").append(pathElement);
    }
    return sb.toString();
  }
}
