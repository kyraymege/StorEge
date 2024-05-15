package com.kyraymege.StorEge.utils;

import com.kyraymege.StorEge.entity.dto.UserDto;
import com.kyraymege.StorEge.entity.concretes.Credential;
import com.kyraymege.StorEge.entity.concretes.Role;
import com.kyraymege.StorEge.entity.concretes.User;
import com.kyraymege.StorEge.exceptions.APIException;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static com.kyraymege.StorEge.utils.consts.Constants.CRED_EXPIRED_DAYS;
import static com.kyraymege.StorEge.utils.consts.Constants.STOREGE;
import static dev.samstevens.totp.util.Utils.getDataUriForImage;
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
                .qrCodeImageUri(EMPTY)
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

    public static BiFunction<String, String, QrData> qrCodeFunction = (email, secret) -> new QrData.Builder()
            .issuer(STOREGE)
            .label(email)
            .secret(secret)
            .algorithm(HashingAlgorithm.SHA1)
            .digits(6)
            .period(30)
            .build();

    public static BiFunction<String, String, String> qrCodeImageUri = (email, secret) -> {
        var data = qrCodeFunction.apply(email, secret);
        var generator = new ZxingPngQrGenerator();
        byte[] imageData;
        try {
            imageData = generator.generate(data);
        } catch (Exception e) {
            throw new APIException("Error generating QR code URI");
        }
        return getDataUriForImage(imageData, generator.getImageMimeType());
    };

    public static Supplier<String> qrCodeSecret = () -> new DefaultSecretGenerator().generate();
}
