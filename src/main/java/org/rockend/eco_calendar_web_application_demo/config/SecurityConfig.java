package org.rockend.eco_calendar_web_application_demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // публичные страницы
                        .requestMatchers("/resources/**", "/", "/calendar", "/eco-front").permitAll()

                        // страница логина доступна всем
                        .requestMatchers("/login-to-admin").permitAll()

                        // GET /api/events — всем (для просмотра календаря)
                        .requestMatchers(HttpMethod.GET, "/api/events/**").permitAll()

                        // админские страницы и операции
                        .requestMatchers("/admin-panel", "/delete-event", "/add").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/events/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/events/**").hasRole("ADMIN")

                        // остальное пока не трогаем
                        .anyRequest().permitAll()
                )

                // Форма логина
                .formLogin(form -> form
                        .loginPage("/login-to-admin")          // GET-страница с формой
                        .loginProcessingUrl("/login-to-admin") // POST сюда
                        .usernameParameter("email")            // как мы назовём поле в форме
                        .passwordParameter("password")
                        .defaultSuccessUrl("/admin-panel", true)
                        .permitAll()
                )

                // logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/calendar")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // In-memory админ с *почтой* как логином
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.builder()
                .username("admin@eco-calendar.ru")        // логинимся по этому email
                .password(encoder.encode("admin123"))     // пароль
                .roles("ADMIN")                           // роль -> ROLE_ADMIN
                .build();

        return new InMemoryUserDetailsManager(admin);
    }
}
