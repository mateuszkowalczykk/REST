package pl.mateuszkowalczykk.rest.gateway;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import pl.mateuszkowalczykk.rest.services.user.User;
import pl.mateuszkowalczykk.rest.services.user.UserNotFoundException;
import pl.mateuszkowalczykk.rest.services.user.UserService;

@WebMvcTest
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Test
  void shouldRespondWithCorrectJsonBody() throws Exception {
    // given
    User user = dummyUser();
    String login = user.getLogin();

    given(userService.findByLogin(login)).willReturn(user);

    // when
    ResultActions response = mockMvc.perform(get("/users/" + login));

    // then
    then(userService).should(times(1)).findByLogin(any());

    response
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            content()
                .json(
                    "{\"id\":12345,\"login\":\"dummyLogin\",\"name\":\"Dummy Name\",\"type\":\"User\",\"avatarUrl\":\"https://avatars.githubusercontent.com/u/583231?v=4\",\"createdAt\":\"2011-01-25T18:44:36Z\",\"calculations\":1.0}"));
  }

  @Test
  void shouldRespondWithNotFoundStatus() throws Exception {
    // given
    String login = "anyLogin";
    given(userService.findByLogin(login)).willThrow(new UserNotFoundException(login));

    // when
    ResultActions response = mockMvc.perform(get("/users/"+login));

    // then
    then(userService).should(times(1)).findByLogin(any());

    response
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.type").value("Not Found"))
        .andExpect(jsonPath("$.message").value("Can't find user with login: 'anyLogin'."))
        .andExpect(jsonPath("$.requestDetails").value("uri=/users/" + login));
  }

  private User dummyUser() {
    return User.builder()
        .id(12345L)
        .login("dummyLogin")
        .name("Dummy Name")
        .avatarUrl("https://avatars.githubusercontent.com/u/583231?v=4")
        .type("User")
        .createdAt("2011-01-25T18:44:36Z")
        .calculations(1d)
        .build();
  }
}
