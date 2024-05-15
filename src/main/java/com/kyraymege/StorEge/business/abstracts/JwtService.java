package com.kyraymege.StorEge.business.abstracts;

import com.kyraymege.StorEge.domain.Token;
import com.kyraymege.StorEge.domain.TokenData;
import com.kyraymege.StorEge.entity.dto.UserDto;
import com.kyraymege.StorEge.entity.enums.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;
import java.util.function.Function;

public interface JwtService {
    String createToken(UserDto user, Function<Token,String> tokenFunction);
    Optional<String> extractToken(HttpServletRequest request, String tokenType);
    void addCookie(HttpServletResponse response, UserDto user, TokenType type);
    <T> T getTokenData(String token, Function<TokenData,T> tokenDataFunction);
    void removeCookie(HttpServletRequest request,HttpServletResponse response, String cookieName);
}
