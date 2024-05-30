package com.kyraymege.StorEge.controllers;

import com.kyraymege.StorEge.business.abstracts.JwtService;
import com.kyraymege.StorEge.business.abstracts.UserService;
import com.kyraymege.StorEge.domain.Response;
import com.kyraymege.StorEge.entity.dto.UserDto;
import com.kyraymege.StorEge.entity.dto.dtoRequest.*;
import com.kyraymege.StorEge.entity.enums.TokenType;
import com.kyraymege.StorEge.exceptions.handler.ApiLogoutHandler;
import com.kyraymege.StorEge.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static com.kyraymege.StorEge.utils.consts.Constants.FILE_DIRECTORY;
import static java.util.Collections.emptyMap;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@RestController
@RequestMapping(path = {"/user"})
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
    private final ApiLogoutHandler apiLogoutHandler;

    //////////////////////////// USER REGISTIRATION - START ////////////////////////////
    // USER REGISTRATION
    @PostMapping("/register")
    public ResponseEntity<Response> saveUser(@RequestBody @Valid UserRequest user, HttpServletRequest request) {
        userService.createUser(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword());
        return ResponseEntity.created(URI.create("")).body(RequestUtils.getResponse(request, emptyMap(), "Account Created. Check your email to activate your account.", HttpStatus.CREATED));
    }

    // USER EMAIL VERIFICATION
    @GetMapping("/verify/accountVerification")
    public ResponseEntity<Response> verifyAccount(@RequestParam("token") String token, HttpServletRequest request) {
        userService.verifyAccountToken(token);
        return ResponseEntity.ok().body(RequestUtils.getResponse(request, emptyMap(), "Account Verified", HttpStatus.OK));
    }

    // USER PROFILE
    @GetMapping("/profile")
    @PreAuthorize("hasAnyAuthority('user:read') or hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Response> getProfile(@AuthenticationPrincipal UserDto userPrincipal, HttpServletRequest request) {
        var user = userService.getUserByUserId(userPrincipal.getUserId());
        return ResponseEntity.ok().body(RequestUtils.getResponse(request, Map.of("user",user), "Profile retrieved", HttpStatus.OK));
    }

    // USER PROFILE UPDATE
    @PatchMapping("/update")
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN')")
    public ResponseEntity<Response> updateProfile(@AuthenticationPrincipal UserDto userPrincipal,@RequestBody UserRequest userRequest , HttpServletRequest request) {
        var user = userService.updateUser(userPrincipal.getUserId(), userRequest.getFirstName(), userRequest.getLastName(), userRequest.getEmail(),userRequest.getPhone(), userRequest.getBio());
        return ResponseEntity.ok().body(RequestUtils.getResponse(request, Map.of("user",user), "User Updated", HttpStatus.OK));
    }

    // USER PROFILE UPDATE
    @PatchMapping("/updateRole")
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN')")
    public ResponseEntity<Response> updateRole(@AuthenticationPrincipal UserDto userPrincipal, @RequestBody RoleRequest roleRequest , HttpServletRequest request) {
        userService.updateRole(userPrincipal.getUserId(),roleRequest.getRole());
        return ResponseEntity.ok().body(RequestUtils.getResponse(request, emptyMap(), "Role Updated", HttpStatus.OK));
    }

    // USER PROFILE UPDATE
    @PatchMapping("/toogleAccountExpired")
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN')")
    public ResponseEntity<Response> toogleAccountExpired(@AuthenticationPrincipal UserDto userPrincipal , HttpServletRequest request) {
        userService.toogleAccountExpired(userPrincipal.getUserId());
        return ResponseEntity.ok().body(RequestUtils.getResponse(request, emptyMap(), "Role Updated", HttpStatus.OK));
    }

    // USER PROFILE UPDATE
    @PatchMapping("/toogleAccountLocked")
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN')")
    public ResponseEntity<Response> toogleAccountLocked(@AuthenticationPrincipal UserDto userPrincipal , HttpServletRequest request) {
        userService.toogleAccountLocked(userPrincipal.getUserId());
        return ResponseEntity.ok().body(RequestUtils.getResponse(request, emptyMap(), "Role Updated", HttpStatus.OK));
    }

    // USER PROFILE UPDATE
    @PatchMapping("/toogleAccountEnabled")
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN')")
    public ResponseEntity<Response> toogleAccountEnabled(@AuthenticationPrincipal UserDto userPrincipal , HttpServletRequest request) {
        userService.toogleAccountEnabled(userPrincipal.getUserId());
        return ResponseEntity.ok().body(RequestUtils.getResponse(request, emptyMap(), "Role Updated", HttpStatus.OK));
    }

    // USER PROFILE UPDATE
    @PatchMapping("/toogleCredentialsExpired")
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN')")
    public ResponseEntity<Response> toogleCredentialsExpired(@AuthenticationPrincipal UserDto userPrincipal , HttpServletRequest request) {
        userService.toogleCredentialsExpired(userPrincipal.getUserId());
        return ResponseEntity.ok().body(RequestUtils.getResponse(request, emptyMap(), "Role Updated", HttpStatus.OK));
    }

    // USER Update Password
    @PatchMapping("/updatePassword")
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN')")
    public ResponseEntity<Response> updatePassword(@AuthenticationPrincipal UserDto userPrincipal ,@RequestBody UpdatePasswordRequest updatePasswordRequest , HttpServletRequest request) {
        userService.updatePassword(userPrincipal.getUserId(), updatePasswordRequest.getPassword(), updatePasswordRequest.getNewPassword(), updatePasswordRequest.getConfirmNewPassword());
        return ResponseEntity.ok().body(RequestUtils.getResponse(request, emptyMap(), "Role Updated", HttpStatus.OK));
    }

    // USER Update Photo
    @PatchMapping("/photo")
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN')")
    public ResponseEntity<Response> uploadPhoto(@AuthenticationPrincipal UserDto userPrincipal , @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        var imageUrl = userService.uploadPhoto(userPrincipal.getUserId(), file);
        return ResponseEntity.ok().body(RequestUtils.getResponse(request, Map.of("imageUrl",imageUrl), "Role Updated", HttpStatus.OK));
    }

    // User Show Photo
    @GetMapping(value = "/image/{fileName}", produces = {IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE})
    public byte[] getPhoto(@PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(FILE_DIRECTORY + fileName));
    }

    // USER Logout
    @PatchMapping("/logout")
    public ResponseEntity<Response> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        apiLogoutHandler.logout(request, response, authentication);
        return ResponseEntity.ok().body(RequestUtils.getResponse(request, emptyMap(), "Logged Out Successfully", HttpStatus.OK));
    }

    //////////////////////////// MFA/2FA - START ////////////////////////////
    // MFA/2FA SETUP
    @PatchMapping("/mfa/setup")
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Response> setupMfa(@AuthenticationPrincipal UserDto userPrincipal, HttpServletRequest request) {
        var user = userService.setupMfa(userPrincipal.getId());
        return ResponseEntity.ok().body(RequestUtils.getResponse(request, Map.of("user", user), "MFA set up success", HttpStatus.OK));
    }

    // MFA/2FA CANCEL
    @PatchMapping("/mfa/cancel")
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Response> cancelMfa(@AuthenticationPrincipal UserDto userPrincipal, HttpServletRequest request) {
        var user = userService.cancelMfa(userPrincipal.getId());
        return ResponseEntity.ok().body(RequestUtils.getResponse(request, Map.of("user", user), "MFA canceled successfully", HttpStatus.OK));
    }

    // VERIFY QR CODE
    @PostMapping("/verify/qrcode")
    public ResponseEntity<Response> verifyQrCode(@RequestBody QrCodeRequest qrCodeRequest, HttpServletResponse response, HttpServletRequest request) {
        var user = userService.verifyQrCode(qrCodeRequest.getUserId(), qrCodeRequest.getQrCode());
        jwtService.addCookie(response, user, TokenType.ACCESS);
        jwtService.addCookie(response, user, TokenType.REFRESH);
        return ResponseEntity.ok().body(RequestUtils.getResponse(request, Map.of("user", user), "Qr Code Verified", HttpStatus.OK));
    }

    //////////////////////////// RESET PASSWORD - START ////////////////////////////
    // RESET PASSWORD
    @PostMapping("/resetpassword")
    public ResponseEntity<Response> resetPassword(@RequestBody @Valid EmailRequest emailRequest, HttpServletRequest request) {
        userService.resetPassword(emailRequest.getEmail());
        return ResponseEntity.ok().body(RequestUtils.getResponse(request, emptyMap(), "Password reset link sent to your email", HttpStatus.OK));
    }

    // VERIFY RESET PASSWORD TOKEN
    @GetMapping("/verify/password")
    public ResponseEntity<Response> verifyResetPasswordToken(@RequestParam("token") String token, HttpServletRequest request) {
        var user = userService.verifyResetPasswordToken(token);
        return ResponseEntity.ok().body(RequestUtils.getResponse(request, Map.of("user", user), "Enter new password", HttpStatus.OK));
    }

    // RESET PASSWORD BY USER
    @PostMapping("/resetpassword/reset")
    public ResponseEntity<Response> resetPasswordReset(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest, HttpServletRequest request) {
        userService.updatePassword(resetPasswordRequest.getUserId(),resetPasswordRequest.getNewPassword(),resetPasswordRequest.getConfirmNewPassword());
        return ResponseEntity.ok().body(RequestUtils.getResponse(request, emptyMap(), "Password has been changed!", HttpStatus.OK));
    }
}
