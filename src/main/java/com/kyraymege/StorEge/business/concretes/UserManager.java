package com.kyraymege.StorEge.business.concretes;

import com.kyraymege.StorEge.business.abstracts.UserService;
import com.kyraymege.StorEge.cache.CacheStore;
import com.kyraymege.StorEge.domain.RequestContext;
import com.kyraymege.StorEge.entity.dto.UserDto;
import com.kyraymege.StorEge.entity.concretes.Confirmation;
import com.kyraymege.StorEge.entity.concretes.Credential;
import com.kyraymege.StorEge.entity.concretes.Role;
import com.kyraymege.StorEge.entity.concretes.User;
import com.kyraymege.StorEge.entity.enums.Authority;
import com.kyraymege.StorEge.entity.enums.EventType;
import com.kyraymege.StorEge.entity.enums.LoginType;
import com.kyraymege.StorEge.event.UserEvent;
import com.kyraymege.StorEge.exceptions.APIException;
import com.kyraymege.StorEge.repositories.ConfirmationRepository;
import com.kyraymege.StorEge.repositories.CredentialsRepository;
import com.kyraymege.StorEge.repositories.RoleRepository;
import com.kyraymege.StorEge.repositories.UserRepository;
import com.kyraymege.StorEge.utils.UserUtils;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.BiFunction;

import static com.kyraymege.StorEge.utils.UserUtils.*;
import static com.kyraymege.StorEge.utils.consts.Constants.PHOTO_DIRECTORY;
import static com.kyraymege.StorEge.utils.validation.UserValidation.verifyAccountStatus;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class UserManager implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CredentialsRepository credentialsRepository;
    private final ConfirmationRepository confirmationRepository;
    private final BCryptPasswordEncoder encoder;
    private final CacheStore<String,Integer> userCache;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void createUser(String firstName, String lastName, String email, String password) {
        var user = userRepository.save(createNewUser(firstName, lastName, email));
        var credentials = new Credential(user, encoder.encode(password));
        credentialsRepository.save(credentials);
        var confirmation = new Confirmation(user);
        confirmationRepository.save(confirmation);
        applicationEventPublisher.publishEvent(new UserEvent(user, EventType.REGISTIRATION, Map.of("key", confirmation.getKey())));
    }

    @Override
    public Role getRoleName(String name) {
        var role = roleRepository.findByNameIgnoreCase(name);
        return role.orElseThrow(() -> new APIException("Role not found"));
    }

    @Override
    public void verifyAccountToken(String token) {
        var confirmation = getUserConfirmation(token);
        var user = getUserEntityByEmail(confirmation.getUser().getEmail());
        user.setEnabled(true);
        userRepository.save(user);
        confirmationRepository.delete(confirmation);
    }

    @Override
    public void updateLoginAttempt(String email, LoginType loginType) {
        var user = getUserEntityByEmail(email);
        RequestContext.setUserId(user.getId());
        switch (loginType) {
            case LOGIN_ATTEMPT -> {
                if (userCache.get(user.getEmail()) == null) {
                    user.setLoginAttempts(0);
                    user.setAccountNonLocked(true);
                }
                user.setLoginAttempts(user.getLoginAttempts() + 1);
                userCache.put(user.getEmail(), user.getLoginAttempts());
                if (userCache.get(user.getEmail()) >= 5) {
                    user.setAccountNonLocked(false);
                }
            }
            case LOGIN_SUCCESS -> {
                user.setAccountNonLocked(true);
                user.setLoginAttempts(0);
                user.setLastLogin(LocalDateTime.now());
                userCache.remove(user.getEmail());
            }
        }
        userRepository.save(user);
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        var user = userRepository.findUserByUserId(userId).orElseThrow(() -> new APIException("User not found"));
        return EntityToDto(user, user.getRole(), getUserCredentialById(user.getId()));
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = getUserEntityByEmail(email);
        return EntityToDto(user, user.getRole(), getUserCredentialById(user.getId()));
    }

    @Override
    public Credential getUserCredentialById(Long userId) {
        var credentialByUserId = credentialsRepository.getCredentialByUserId(userId);
        return credentialByUserId.orElseThrow(() -> new APIException("Credential not found"));
    }

    @Override
    public UserDto setupMfa(Long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new APIException("User not found"));
        var codeSecret = qrCodeSecret.get();
        user.setQrCodeImageUri(qrCodeImageUri.apply(user.getEmail(), codeSecret));
        user.setQrCodeSecret(codeSecret);
        user.setMfa(true);
        userRepository.save(user);
        return EntityToDto(user, user.getRole(), getUserCredentialById(user.getId()));
    }

    @Override
    public UserDto cancelMfa(Long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new APIException("User not found"));
        user.setQrCodeSecret("");
        user.setQrCodeImageUri("");
        user.setMfa(false);
        userRepository.save(user);
        return EntityToDto(user, user.getRole(), getUserCredentialById(user.getId()));
    }

    @Override
    public UserDto verifyQrCode(String userId, String qrCode) {
        var user = userRepository.findUserByUserId(userId).orElseThrow(() -> new APIException("User not found"));
        verifyCode(qrCode, user.getQrCodeSecret());
        return EntityToDto(user, user.getRole(), getUserCredentialById(user.getId()));
    }

    @Override
    public void resetPassword(String email) {
        var user = getUserEntityByEmail(email);
        var confirmation = getUserConfirmation(user);
        if(confirmation != null) {
            applicationEventPublisher.publishEvent(new UserEvent(user, EventType.RESETPASSWORD, Map.of("key", confirmation.getKey())));
        }else{
            var newConfirmation = new Confirmation(user);
            confirmationRepository.save(newConfirmation);
            applicationEventPublisher.publishEvent(new UserEvent(user, EventType.RESETPASSWORD, Map.of("key", newConfirmation.getKey())));
        }
    }

    @Override
    public UserDto verifyResetPasswordToken(String token) {
        var confirmation = getUserConfirmation(token);
        var user = getUserEntityByEmail(confirmation.getUser().getEmail());
        verifyAccountStatus(user);
        confirmationRepository.delete(confirmation);
        return EntityToDto(user, user.getRole(), getUserCredentialById(user.getId()));
    }

    @Override
    public void updatePassword(String userId, String newPassword, String confirmNewPassword) {
        if(!newPassword.equals(confirmNewPassword)){ throw new APIException("Passwords do not match");}
        var user = getUserByUserId(userId);
        var credential = getUserCredentialById(user.getId());
        credential.setPassword(encoder.encode(newPassword));
        credentialsRepository.save(credential);
    }

    @Override
    public UserDto updateUser(String userId, String firstName, String lastName, String email, String phone, String bio) {
        var user = getUserEntityByUserId(userId);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setBio(bio);
        userRepository.save(user);
        return EntityToDto(user, user.getRole(), getUserCredentialById(user.getId()));
    }

    @Override
    public void updateRole(String userId, String role) {
        var user = getUserEntityByUserId(userId);
        user.setRole(getRoleName(role));
        userRepository.save(user);
    }

    @Override
    public void toogleAccountExpired(String userId) {
        var user = getUserEntityByUserId(userId);
        user.setAccountNonExpired(!user.isAccountNonExpired());
        userRepository.save(user);
    }

    @Override
    public void toogleAccountLocked(String userId) {
        var user = getUserEntityByUserId(userId);
        user.setAccountNonLocked(!user.isAccountNonLocked());
        userRepository.save(user);
    }

    @Override
    public void toogleAccountEnabled(String userId) {
        var user = getUserEntityByUserId(userId);
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    // TODO: This logic is incorrect. It should be updated. Auditable will update data when it's saved.
    @Override
    public void toogleCredentialsExpired(String userId) {
        var user = getUserEntityByUserId(userId);
        var credential = getUserCredentialById(user.getId());
        if (credential.getUpdatedAt().plusDays(90).isAfter(LocalDateTime.now())) {
            credential.setUpdatedAt(LocalDateTime.now());
        } else {
            credential.setUpdatedAt(LocalDateTime.of(2000, 1, 1, 0, 0, 0));
        }
        userRepository.save(user);
    }

    @Override
    public void updatePassword(String userId, String password, String newPassword, String confirmNewPassword) {
        if(!newPassword.equals(confirmNewPassword)){ throw new APIException("Passwords do not match");}
        var user = getUserEntityByUserId(userId);
        verifyAccountStatus(user);
        var credential = getUserCredentialById(user.getId());
        if(!encoder.matches(password, credential.getPassword())){ throw new APIException("Password is incorrect"); }
        credential.setPassword(encoder.encode(newPassword));
        credentialsRepository.save(credential);
    }

    @Override
    public String uploadPhoto(String userId, MultipartFile file) {
        var user = getUserEntityByUserId(userId);
        var photoUrl = photoFunction.apply(userId,file);
        user.setProfilePicture(photoUrl+"?timestamp="+System.currentTimeMillis());
        userRepository.save(user);
        return "";
    }

    //TODO: Will be move to Amazon S3 or CloudFlare or Cloudinary
    private final BiFunction<String,MultipartFile,String> photoFunction = (userId, file) -> {
        var fileName = userId+".png";
        try {
            var fileStorageLocation = Paths.get(PHOTO_DIRECTORY).toAbsolutePath().normalize();
            if(!Files.exists(fileStorageLocation)){
                Files.createDirectories(fileStorageLocation);
            }
            Files.copy(file.getInputStream(), fileStorageLocation.resolve(fileName), REPLACE_EXISTING);
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/user/image/"+ fileName)
                    .toUriString();
        }catch (Exception e){
            throw new APIException("Photo upload failed");
        }
    };

    private User getUserEntityByUserId(String userId) {
        return userRepository.findUserByUserId(userId).orElseThrow(() -> new APIException("User not found"));
    }

    private boolean verifyCode(String qrCode, String qrCodeSecret) {
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        CodeVerifier codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        if (codeVerifier.isValidCode(qrCodeSecret, qrCode)) {
            return true;
        } else {
            throw new APIException("Code is not valid. Please try again.");
        }
    }

    private User getUserEntityByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElseThrow(() -> new APIException("User not found"));
    }

    private Confirmation getUserConfirmation(String token) {
        return confirmationRepository.findByKey(token).orElseThrow(()-> new APIException("Confirmation not found"));
    }

    private Confirmation getUserConfirmation(User user) {
        return confirmationRepository.findByUser(user).orElse(null);
    }

    private User createNewUser(String firstName, String lastName, String email) {
        var role = getRoleName(Authority.USER.name());
        return UserUtils.createUserEntity(firstName, lastName, email,role);
    }
}
