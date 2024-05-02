package com.kyraymege.StorEge.security;

import com.kyraymege.StorEge.business.abstracts.JwtService;
import com.kyraymege.StorEge.business.abstracts.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApiHttpConfigurer extends AbstractHttpConfigurer<ApiHttpConfigurer, HttpSecurity>{

    private final ApiAuthorizationFilter apiAuthorizationFilter;
    private final ApiAuthenticationProvider apiAuthenticationProvider;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Override
    public void init(HttpSecurity http) throws Exception {
        http.authenticationProvider(apiAuthenticationProvider);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(apiAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(new ApiAuthenticationFilter(authenticationConfiguration.getAuthenticationManager(), userService, jwtService), UsernamePasswordAuthenticationFilter.class);
    }
}
