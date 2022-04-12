package pl.mateuszkowalczykk.rest.services.user.githubclient;

public class GithubClientException extends RuntimeException {

  public GithubClientException(Exception e) {
    super(e);
  }
}