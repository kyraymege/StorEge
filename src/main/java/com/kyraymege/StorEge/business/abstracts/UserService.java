package com.kyraymege.StorEge.business.abstracts;

import com.kyraymege.StorEge.domain.RequestContext;
import com.kyraymege.StorEge.dto.UserDto;
import com.kyraymege.StorEge.entity.Credential;
import com.kyraymege.StorEge.entity.Role;
import com.kyraymege.StorEge.entity.User;
import com.kyraymege.StorEge.enums.Authority;
import com.kyraymege.StorEge.enums.LoginType;

import java.util.Optional;

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
}
