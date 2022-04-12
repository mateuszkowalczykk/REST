package pl.mateuszkowalczykk.rest.services.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.mateuszkowalczykk.rest.services.user.githubclient.GithubClient;
import pl.mateuszkowalczykk.rest.services.user.githubclient.GithubUser;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @InjectMocks
  private UserService userService;

  @Mock
  private GithubClient githubClient;

  @Test
  void shouldReturnMappedGithubUser() {
    //given
    GithubUser githubUser = dummyGithubUser();
    String login = githubUser.getLogin();

    given(githubClient.getGithubUserByLogin(login))
        .willReturn(Optional.of(githubUser));

    //when
    User resultUser = userService.findByLogin(login);

    //then
    then(githubClient)
        .should(times(1))
        .getGithubUserByLogin(any());

    assertThat(resultUser)
        .hasFieldOrPropertyWithValue("id", githubUser.getId())
        .hasFieldOrPropertyWithValue("login", githubUser.getLogin())
        .hasFieldOrPropertyWithValue("name", githubUser.getName())
        .hasFieldOrPropertyWithValue("avatarUrl", githubUser.getAvatarUrl())
        .hasFieldOrPropertyWithValue("type", githubUser.getType())
        .hasFieldOrPropertyWithValue("createdAt", githubUser.getCreatedAt())
        .hasFieldOrPropertyWithValue("calculations", 16d);
  }

  @Test
  void shouldThrowUserNotFoundException() {
    //given
    String login = "dummyLogin";

    given(githubClient.getGithubUserByLogin(login))
        .willReturn(Optional.empty());

    //when
    //then
    assertThatThrownBy(() -> userService.findByLogin(login))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessage("Can't find user with login: " + login);

    then(githubClient)
        .should(times(1))
        .getGithubUserByLogin(any());
  }

  private GithubUser dummyGithubUser() {
    GithubUser githubUser = new GithubUser();
    githubUser.setId(12345L);
    githubUser.setLogin("dummyLogin");
    githubUser.setName("Dummy Name");
    githubUser.setAvatarUrl("https://avatars.githubusercontent.com/u/583231?v=4");
    githubUser.setType("User");
    githubUser.setCreatedAt("2011-01-25T18:44:36Z");
    githubUser.setPublicRepositories(6);
    githubUser.setFollowers(3);
    return githubUser;
  }
}
