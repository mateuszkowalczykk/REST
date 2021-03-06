package pl.mateuszkowalczykk.rest.gateway.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.mateuszkowalczykk.rest.domainevents.apievents.GetUserByLoginApiEvent;
import pl.mateuszkowalczykk.rest.domainevents.DomainEventPublisher;
import pl.mateuszkowalczykk.rest.services.user.User;
import pl.mateuszkowalczykk.rest.services.user.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final DomainEventPublisher domainEventPublisher;

  @GetMapping(value = "/{login}", produces = {"application/json"})
  @ResponseStatus(HttpStatus.OK)
  public User findUserByLogin(@PathVariable("login") String login) {
    // TODO should login be case sensitive?
    domainEventPublisher.publish(new GetUserByLoginApiEvent(login));
    return userService.findByLogin(login);
  }
}
