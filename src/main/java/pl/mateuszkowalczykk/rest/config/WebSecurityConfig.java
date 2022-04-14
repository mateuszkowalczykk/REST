package pl.mateuszkowalczykk.rest.config;

import javax.servlet.http.HttpServletResponse;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable()
        .authorizeRequests()
        .antMatchers("/users/*").permitAll()
        .antMatchers("/h2-console/**").permitAll()
        .anyRequest().denyAll()
        .and()
        .exceptionHandling()
        .authenticationEntryPoint(
            (request, response, e) -> {
              response.setContentType("application/json;charset=UTF-8");
              response.setStatus(HttpServletResponse.SC_FORBIDDEN);
              response
                  .getWriter()
                  .write(
                      "{\"status\": 403, \"type\":\"Forbidden\", \"message\":\"Access denied\"}");
            });

    // For h2 console frame
    http.headers().frameOptions().sameOrigin();
  }
}
