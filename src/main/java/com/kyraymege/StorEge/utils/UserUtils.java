package com.kyraymege.StorEge.utils;

import com.kyraymege.StorEge.dto.UserDto;
import com.kyraymege.StorEge.entity.Credential;
import com.kyraymege.StorEge.entity.Role;
import com.kyraymege.StorEge.entity.User;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.kyraymege.StorEge.consts.Constants.CRED_EXPIRED_DAYS;
import static org.apache.logging.log4j.util.Strings.EMPTY;

public class UserUtils {
    public static User createUserEntity(String firstName, String lastName, String email, Role role){
        return User.builder()
                .userId(UUID.randomUUID().toString())
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .lastLogin(LocalDateTime.now())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .mfa(false)
                .enabled(false)
                .loginAttempts(0)
                .qrCodeSecret(EMPTY)
                .phone(EMPTY)
                .bio(EMPTY)
                .profilePicture("https://t3.ftcdn.net/jpg/03/58/90/78/360_F_358907879_Vdu96gF4XVhjCZxN2kCG0THTsSQi8IhT.jpg")
                .role(role)
                .build();

    }

    public static UserDto EntityToDto(User user, Role role, Credential userCredential) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
        userDto.setLastLogin(user.getLastLogin().toString());
        userDto.setCredentialsNonExpired(isCredentialsNonExpired(userCredential));
        userDto.setCreatedAt(user.getCreatedAt().toString());
        userDto.setUpdatedAt(user.getUpdatedAt().toString());
        userDto.setRole(role.getName());
        userDto.setAuthorities(role.getAuthorities().getValue());
        return userDto;
    }

    public static boolean isCredentialsNonExpired(Credential userCredential) {
        return userCredential.getUpdatedAt().minusDays(CRED_EXPIRED_DAYS).isAfter(LocalDateTime.now());
    }
}
