package pl.mateuszkowalczykk.rest.services.user;

public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(String login) {
    super("Can't find user with login: '" + login + "'.");
  }
}
