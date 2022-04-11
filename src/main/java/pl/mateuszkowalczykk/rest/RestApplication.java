package pl.mateuszkowalczykk.rest;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RestApplication {

  public static void main(String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone("Poland"));
    SpringApplication.run(RestApplication.class, args);
  }

}
