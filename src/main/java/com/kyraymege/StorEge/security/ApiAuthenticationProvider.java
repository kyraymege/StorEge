package com.kyraymege.StorEge.security;

import com.kyraymege.StorEge.business.abstracts.UserService;
import com.kyraymege.StorEge.domain.ApiAuthentication;
import com.kyraymege.StorEge.domain.UserPrincipal;
import com.kyraymege.StorEge.exceptions.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.kyraymege.StorEge.utils.consts.Constants.CRED_EXPIRED_DAYS;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApiAuthenticationProvider implements AuthenticationProvider{

    private final UserService userService;
    private final BCryptPasswordEncoder encoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var apiAuthentication = authenticationFunction.apply(authentication);
        var user = userService.getUserByEmail(apiAuthentication.getEmail());
        log.info(String.valueOf(user));
        if (user != null) {
            var userCredential = userService.getUserCredentialById(user.getId());
            if(!userCredential.getUpdatedAt().plusDays(CRED_EXPIRED_DAYS).isAfter(LocalDateTime.now())){
                throw new APIException("Password expired. Please reset your password");
            }
            var userPrincipal = new UserPrincipal(user, userCredential);
            log.info(String.valueOf(user));
            validAccount.accept(userPrincipal);
            if(encoder.matches(apiAuthentication.getPassword(), userCredential.getPassword())){
               return ApiAuthentication.authenticated(user, userPrincipal.getAuthorities());
            } else throw new BadCredentialsException("Email and/or Password is incorrect");
        } else throw new APIException("Unable to authenticate");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiAuthentication.class.isAssignableFrom(authentication);
    }

    private final Function<Authentication, ApiAuthentication> authenticationFunction = authentication -> (ApiAuthentication) authentication;
    private final Consumer<UserPrincipal> validAccount = userPrincipal -> {
        if (!userPrincipal.isAccountNonLocked()) throw new LockedException("Account is locked");
        if (!userPrincipal.isEnabled()) throw new DisabledException("Account is disabled");
        if (!userPrincipal.isCredentialsNonExpired()) throw new CredentialsExpiredException("Password is expired");
        if (!userPrincipal.isAccountNonExpired()) throw new DisabledException("Account is expired");
    };


}
