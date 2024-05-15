package com.kyraymege.StorEge.domain;

import com.kyraymege.StorEge.entity.dto.UserDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import io.jsonwebtoken.Claims;

import java.util.List;

@Builder
@Getter
@Setter
public class TokenData {
    private UserDto user;
    private Claims claims;
    private boolean valid;
    private List<GrantedAuthority> authorities;
}
