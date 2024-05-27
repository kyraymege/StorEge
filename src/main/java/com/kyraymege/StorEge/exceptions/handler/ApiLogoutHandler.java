package com.kyraymege.StorEge.exceptions.handler;

import com.kyraymege.StorEge.business.abstracts.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import static com.kyraymege.StorEge.entity.enums.TokenType.ACCESS;
import static com.kyraymege.StorEge.entity.enums.TokenType.REFRESH;

@Component
@RequiredArgsConstructor
public class ApiLogoutHandler implements LogoutHandler {
    private final JwtService jwtService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, authentication);
        jwtService.removeCookie(request,response, ACCESS.getValue());
        jwtService.removeCookie(request,response, REFRESH.getValue());
    }
}
