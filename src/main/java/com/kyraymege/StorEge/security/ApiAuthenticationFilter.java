package com.kyraymege.StorEge.security;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyraymege.StorEge.business.abstracts.JwtService;
import com.kyraymege.StorEge.business.abstracts.UserService;
import com.kyraymege.StorEge.domain.ApiAuthentication;
import com.kyraymege.StorEge.domain.Response;
import com.kyraymege.StorEge.entity.dto.UserDto;
import com.kyraymege.StorEge.entity.dto.dtoRequest.LoginRequest;
import com.kyraymege.StorEge.entity.enums.LoginType;
import com.kyraymege.StorEge.entity.enums.TokenType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Map;

import static com.kyraymege.StorEge.utils.RequestUtils.getResponse;
import static com.kyraymege.StorEge.utils.RequestUtils.handleErrorResponse;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class ApiAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final UserService userService;
    private final JwtService jwtService;

    protected ApiAuthenticationFilter(AuthenticationManager authenticationManager, UserService userService, JwtService jwtService) {
        super(new AntPathRequestMatcher("/user/login", "POST"), authenticationManager);
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        try{
            var user = new ObjectMapper().configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true).readValue(request.getInputStream(), LoginRequest.class);
            userService.updateLoginAttempt(user.getEmail(), LoginType.LOGIN_ATTEMPT);
            var authentication = ApiAuthentication.unauthenticated(user.getEmail(), user.getPassword());
            return getAuthenticationManager().authenticate(authentication);
        }catch (Exception e){
            log.error("Error while authenticating user", e);
            handleErrorResponse(request,response,e);
            return null;
        }

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        var user = (UserDto) authentication.getPrincipal();
        userService.updateLoginAttempt(user.getEmail(), LoginType.LOGIN_SUCCESS);
        var httpResponse = user.isMfa() ? sendQrCode(request, user) : sendResponse(request,response, user);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(OK.value());
        var out = response.getOutputStream();
        var mapper = new ObjectMapper();
        mapper.writeValue(out,httpResponse);
        out.flush();
    }

    private Response sendQrCode(HttpServletRequest request, UserDto user) {
        return getResponse(request, Map.of("user", user), "Please Enter the QR Code", OK);
    }

    private Response sendResponse(HttpServletRequest request, HttpServletResponse response, UserDto user) {
        jwtService.addCookie(response,user, TokenType.ACCESS);
        jwtService.addCookie(response,user, TokenType.REFRESH);
        return getResponse(request, Map.of("user", user), "Login Success", OK);
    }
}
