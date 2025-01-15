package cz.cvut.fel.ear.semestralka.security;

import cz.cvut.fel.ear.semestralka.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/admins/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/facilities/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/facilities/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/facilities/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/facilities/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/payments/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/payments/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/reservations/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/reservations/{id}").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )
                .httpBasic(withDefaults());
        return http.build();
    }
}