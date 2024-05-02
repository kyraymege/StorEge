package com.kyraymege.StorEge.business.concretes;

import com.kyraymege.StorEge.business.abstracts.JwtService;
import com.kyraymege.StorEge.business.abstracts.UserService;
import com.kyraymege.StorEge.domain.Token;
import com.kyraymege.StorEge.domain.TokenData;
import com.kyraymege.StorEge.dto.UserDto;
import com.kyraymege.StorEge.enums.TokenType;
import com.kyraymege.StorEge.function.TriConsumer;
import com.kyraymege.StorEge.security.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.kyraymege.StorEge.consts.Constants.*;
import static io.jsonwebtoken.Header.JWT_TYPE;
import static io.jsonwebtoken.Header.TYPE;
import static java.util.Arrays.stream;
import static java.util.Optional.empty;
import static org.apache.tomcat.util.http.SameSiteCookies.NONE;
import static org.springframework.security.core.authority.AuthorityUtils.commaSeparatedStringToAuthorityList;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtManager extends JwtConfig implements JwtService {

    private final UserService userService;

    private final Supplier<SecretKey> key = () -> Keys.hmacShaKeyFor(Decoders.BASE64.decode(getSecret()));

    private final Function<String, Claims> claimsFunction = token -> Jwts.parser()
            .verifyWith(key.get())
            .build()
            .parseSignedClaims(token)
            .getPayload();

    private final Function<String,String> subject = token -> getClaimsValue(token,Claims::getSubject);

    private final BiFunction<HttpServletRequest, String, Optional<String>> extractToken = (request,cookieName) ->
            Optional.of(stream(request.getCookies() == null ? new Cookie[]{new Cookie(EMPTY_VALUE,EMPTY_VALUE)} : request.getCookies())
                    .filter(cookie -> Objects.equals(cookieName, cookie.getName()))
                    .map(Cookie::getValue)
                    .findAny())
                    .orElse(empty());

    private final BiFunction<HttpServletRequest,String, Optional<Cookie>> extractCookie = (request,cookieName) ->
            Optional.of(stream(request.getCookies() == null ? new Cookie[]{new Cookie(EMPTY_VALUE,EMPTY_VALUE)} : request.getCookies())
                    .filter(cookie -> Objects.equals(cookieName, cookie.getName()))
                    .findAny())
                    .orElse(empty());

    private final Supplier<JwtBuilder> builder = () ->
            Jwts.builder()
                    .header()
                    .add(Map.of(TYPE, JWT_TYPE))
                    .and()
                    .audience().add(STOREGE)
                    .and()
                    .id(UUID.randomUUID().toString())
                    .issuedAt(Date.from(Instant.now()))
                    .notBefore(new Date())
                    .signWith(key.get(), Jwts.SIG.HS512);

    private final BiFunction<UserDto, TokenType, String> buildToken = (user, type) ->
            Objects.equals(type, TokenType.ACCESS) ? builder.get()
                    .subject(user.getUserId())
                    .claim(AUTHORITIES, user.getAuthorities())
                    .claim(ROLE, user.getRole())
                    .expiration(Date.from(Instant.now().plusSeconds(getExpiration())))
                    .compact() : builder.get()
                    .subject(user.getUserId())
                    .expiration(Date.from(Instant.now().plusSeconds(getExpiration())))
                    .compact();

    private final TriConsumer<HttpServletResponse, UserDto, TokenType> addCookie = (response, user, type) -> {
        switch (type){
            case ACCESS->{
                var accessToken = createToken(user, Token::getAccess);
                var cookie = new Cookie(type.getValue(), accessToken);
                cookie.setHttpOnly(true);
//                cookie.setSecure(true);
                cookie.setMaxAge(2*60);
                cookie.setPath("/");
                cookie.setAttribute("SameSite", NONE.name());
                response.addCookie(cookie);
            }
            case REFRESH->{
                var refreshToken = createToken(user, Token::getAccess);
                var cookie = new Cookie(type.getValue(), refreshToken);
                cookie.setHttpOnly(true);
//                cookie.setSecure(true);
                cookie.setMaxAge(2*60*60);
                cookie.setPath("/");
                cookie.setAttribute("SameSite", NONE.name());
                response.addCookie(cookie);
            }
        }

    };

    private <T> T getClaimsValue(String token, Function<Claims, T> claims)  {
        return claimsFunction.andThen(claims).apply(token);
    }

    public Function<String, List<GrantedAuthority>> authorities = token ->
            commaSeparatedStringToAuthorityList(new StringJoiner(AUTHORITY_DELIMITER)
                    .add(claimsFunction.apply(token).get(AUTHORITIES,String.class))
                    .add(ROLE_PREFIX + claimsFunction.apply(token).get(ROLE, String.class)).toString());

    @Override
    public String createToken(UserDto user, Function<Token, String> tokenFunction) {
        var token = Token.builder().access(buildToken.apply(user, TokenType.ACCESS)).refresh(buildToken.apply(user, TokenType.REFRESH)).build();
        return tokenFunction.apply(token);
    }

    @Override
    public Optional<String> extractToken(HttpServletRequest request, String cookieName) {
        return extractToken.apply(request, cookieName);
    }

    @Override
    public void addCookie(HttpServletResponse response, UserDto user, TokenType type) {
        addCookie.accept(response, user, type);
    }

    @Override
    public <T> T getTokenData(String token, Function<TokenData, T> tokenFunction) {
        return tokenFunction.apply(
                TokenData.builder()
                        .valid(Objects.equals(userService.getUserByUserId(subject.apply(token)).getUserId(), claimsFunction.apply(token).getSubject()))
                        .authorities(authorities.apply(token))
                        .claims(claimsFunction.apply(token))
                        .user(userService.getUserByUserId(subject.apply(token)))
                        .build()

        );
    }

    @Override
    public void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        var optionalCookie = extractCookie.apply(request, cookieName);
        if (optionalCookie.isPresent()){
            var cookie = optionalCookie.get();
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }
}
