package com.shrooms.scaffold.config;

import com.shrooms.scaffold.security.AdminInterceptor;
import com.shrooms.scaffold.security.AuthenticationInterceptor;
import com.shrooms.scaffold.security.UserOnlyInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final AuthenticationInterceptor authenticationInterceptor;
    private final AdminInterceptor adminInterceptor;
    private final UserOnlyInterceptor userOnlyInterceptor;

    public WebConfiguration(AuthenticationInterceptor authenticationInterceptor,
                            AdminInterceptor adminInterceptor,
                            UserOnlyInterceptor userOnlyInterceptor) {
        this.authenticationInterceptor = authenticationInterceptor;
        this.adminInterceptor = adminInterceptor;
        this.userOnlyInterceptor = userOnlyInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns(
                        "/users/profile",
                        "/users/profile/**"
                );

        registry.addInterceptor(userOnlyInterceptor)
                .addPathPatterns(
                        "/orders",
                        "/orders/**",
                        "/scaffolds/purchase",
                        "/scaffolds/purchase/**",
                        "/scaffolds/rent",
                        "/scaffolds/rent/**",
                        "/custom-order",
                        "/custom-order/**"
                );

        registry.addInterceptor(adminInterceptor)
                .addPathPatterns(
                        "/admin",
                        "/admin/**"
                );
    }
}