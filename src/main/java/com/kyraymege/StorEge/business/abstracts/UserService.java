package com.kyraymege.StorEge.business.abstracts;

import com.kyraymege.StorEge.entity.dto.UserDto;
import com.kyraymege.StorEge.entity.concretes.Credential;
import com.kyraymege.StorEge.entity.concretes.Role;
import com.kyraymege.StorEge.entity.enums.LoginType;
import org.springframework.web.multipart.MultipartFile;

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

    UserDto updateUser(String userId, String firstName, String lastName, String email, String phone, String bio);

    void updateRole(String userId, String role);

    void toogleAccountExpired(String userId);

    void toogleAccountLocked(String userId);

    void toogleAccountEnabled(String userId);

    void toogleCredentialsExpired(String userId);

    void updatePassword(String userId, String password, String newPassword, String confirmNewPassword);

    String uploadPhoto(String userId, MultipartFile file);

    UserDto getUserById(Long id);
}
