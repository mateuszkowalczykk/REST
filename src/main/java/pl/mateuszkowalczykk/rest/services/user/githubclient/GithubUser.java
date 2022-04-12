package pl.mateuszkowalczykk.rest.services.user.githubclient;

import com.fasterxml.jackson.annotation.JsonProperty;
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
  private String createdAt;

  private int followers;

  @JsonProperty("public_repos")
  private int publicRepositories;
}
