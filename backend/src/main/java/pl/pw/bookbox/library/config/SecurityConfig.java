package pl.pw.bookbox.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // APIs are stateless for now
            .authorizeRequests()
                .requestMatchers("/api/**").permitAll() // allow API calls without auth for simplicity
                .anyRequest().authenticated()
            .and()
            .formLogin().disable();
        return http.build(); // Wymaga zwrócenia SecurityFilterChain
    }
}
