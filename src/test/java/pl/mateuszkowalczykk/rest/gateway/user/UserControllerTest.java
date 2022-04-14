package pl.mateuszkowalczykk.rest.gateway.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import pl.mateuszkowalczykk.rest.domainevents.DomainEventPublisher;
import pl.mateuszkowalczykk.rest.domainevents.apievents.GetUserByLoginApiEvent;
import pl.mateuszkowalczykk.rest.services.user.User;
import pl.mateuszkowalczykk.rest.services.user.UserNotFoundException;
import pl.mateuszkowalczykk.rest.services.user.UserService;

@WebMvcTest
class UserControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private UserService userService;

  @MockBean private DomainEventPublisher domainEventPublisher;

  @Test
  void shouldRespondWithCorrectJsonBodyAndPublishEvent() throws Exception {
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
                    "{\"id\":12345,\"login\":\"dummyLogin\",\"name\":\"Dummy Name\",\"type\":\"User\",\"avatarUrl\":\"https://avatars.githubusercontent.com/u/583231?v=4\",\"createdAt\":\"2011-01-25T18:44:36\",\"calculations\":1.0}"));

    // verify published event
    ArgumentCaptor<GetUserByLoginApiEvent> eventCaptor =
        ArgumentCaptor.forClass(GetUserByLoginApiEvent.class);

    then(domainEventPublisher).should(times(1)).publish(eventCaptor.capture());

    assertThat(eventCaptor.getValue()).returns(login, GetUserByLoginApiEvent::getLogin);
  }

  @Test
  void shouldRespondWithNotFoundStatusAndPublishEvent() throws Exception {
    // given
    String login = "anyLogin";
    given(userService.findByLogin(login)).willThrow(new UserNotFoundException(login));

    // when
    ResultActions response = mockMvc.perform(get("/users/" + login));

    // then
    then(userService).should(times(1)).findByLogin(any());

    response
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.type").value("Not Found"))
        .andExpect(jsonPath("$.message").value("Can't find user with login: 'anyLogin'."))
        .andExpect(jsonPath("$.requestDetails").value("uri=/users/" + login));

    // verify published event
    ArgumentCaptor<GetUserByLoginApiEvent> eventCaptor =
        ArgumentCaptor.forClass(GetUserByLoginApiEvent.class);

    then(domainEventPublisher).should(times(1)).publish(eventCaptor.capture());

    assertThat(eventCaptor.getValue()).returns(login, GetUserByLoginApiEvent::getLogin);
  }

  private User dummyUser() {
    return User.builder()
        .id(12345L)
        .login("dummyLogin")
        .name("Dummy Name")
        .avatarUrl("https://avatars.githubusercontent.com/u/583231?v=4")
        .type("User")
        .createdAt(LocalDateTime.parse("2011-01-25T18:44:36"))
        .calculations(1d)
        .build();
  }
}
