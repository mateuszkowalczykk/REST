package pl.mateuszkowalczykk.rest.services.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.mateuszkowalczykk.rest.services.user.githubclient.GithubClient;
import pl.mateuszkowalczykk.rest.services.user.githubclient.GithubUser;

@Service
@RequiredArgsConstructor
public class UserService {

  private final GithubClient githubClient;

  public User findByLogin(String login) {
    return githubClient.getGithubUserByLogin(login)
        .map(this::toUser)
        .orElseThrow(() -> new UserNotFoundException(login));
  }

  private User toUser(GithubUser githubUser) {
    Double calculations = githubUser.getFollowers() == 0 ?
        // TODO Divide by '0'. Should return null, '-1'/'0' or throw exception?
        null
        : (6d * (2 + githubUser.getPublicRepositories())) / githubUser.getFollowers();

    return User.builder()
        .id(githubUser.getId())
        .login(githubUser.getLogin())
        .name(githubUser.getName())
        .type(githubUser.getType())
        .avatarUrl(githubUser.getAvatarUrl())
        .createdAt(githubUser.getCreatedAt())
        .calculations(calculations)
        .build();
  }
}
