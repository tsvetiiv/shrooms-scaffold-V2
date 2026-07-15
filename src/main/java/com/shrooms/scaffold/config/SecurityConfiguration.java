package com.shrooms.scaffold.config;

import com.shrooms.scaffold.model.dto.user.UserDto;
import com.shrooms.scaffold.model.entity.user.User;
import com.shrooms.scaffold.repository.user.UserRepository;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserRepository userRepository) throws Exception {

        http
                .authorizeHttpRequests(matchers -> matchers
                        .requestMatchers("/owner", "/owner/**").hasRole("OWNER")
                        .requestMatchers("/admin/scaffolds", "/admin/scaffolds/**").hasRole("OWNER")
                        .requestMatchers("/admin", "/admin/**").hasAnyRole("ADMIN", "OWNER")
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                        .permitAll()
                        .requestMatchers("/", "/login", "/register", "/register/success", "/our-work")
                        .permitAll()
                        .anyRequest()
                        .authenticated()

                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            User user = userRepository.findByUsername(authentication.getName())
                                    .orElseThrow();

                            UserDto userDto = UserDto.builder()
                                    .id(user.getId())
                                    .username(user.getUsername())
                                    .firstName(user.getFirstName())
                                    .lastName(user.getLastName())
                                    .email(user.getEmail())
                                    .profilePicture(user.getProfilePicture())
                                    .roleType(user.getRoleType())
                                    .build();

                            request.getSession().setAttribute("user", userDto);
                            switch (userDto.getRoleType()) {
                                case OWNER -> response.sendRedirect("/owner");
                                case ADMIN -> response.sendRedirect("/admin");
                                default -> response.sendRedirect("/");
                            }
                        })
                        .failureHandler((request, response, exception) -> {
                            if (exception instanceof org.springframework.security.authentication.DisabledException) {
                                response.sendRedirect("/login?error=disabled");
                            } else if (exception instanceof org.springframework.security.authentication.LockedException) {
                                response.sendRedirect("/login?error=locked");
                            } else {
                                response.sendRedirect("/login?error=invalid");
                            }
                        })
                        .permitAll())
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessUrl("/")
                );
        return http.build();
    }
}
