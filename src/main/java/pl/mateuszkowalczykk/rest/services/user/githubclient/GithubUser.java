package pl.mateuszkowalczykk.rest.services.user.githubclient;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class GithubUser {

  private Long id;

  private String login;

  private String name;

  private String type;

  @JsonProperty("avatar_url")
  private String avatarUrl;

  @JsonProperty("created_at")
  private LocalDateTime createdAt;

  private int followers;

  @JsonProperty("public_repos")
  private int publicRepositories;
}
