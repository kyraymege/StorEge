package com.kyraymege.StorEge.business.abstracts;

import com.kyraymege.StorEge.entity.dto.UserDto;
import com.kyraymege.StorEge.entity.concretes.Credential;
import com.kyraymege.StorEge.entity.concretes.Role;
import com.kyraymege.StorEge.entity.enums.LoginType;

public interface UserService {

    void createUser(String firstName, String lastName, String email, String password);
    Role getRoleName(String name);
    void verifyAccountToken(String token);
    void updateLoginAttempt(String email, LoginType loginType);
    UserDto getUserByUserId(String userId);
    UserDto getUserByEmail(String email);
    Credential getUserCredentialById(Long id);
    UserDto setupMfa(Long id);

    UserDto cancelMfa(Long id);

    UserDto verifyQrCode(String userId, String qrCode);

    void resetPassword(String email);

    UserDto verifyResetPasswordToken(String token);

    void updatePassword(String userId, String newPassword, String confirmNewPassword);
}
