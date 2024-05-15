package com.kyraymege.StorEge.controllers;

import com.kyraymege.StorEge.business.abstracts.JwtService;
import com.kyraymege.StorEge.business.abstracts.UserService;
import com.kyraymege.StorEge.domain.Response;
import com.kyraymege.StorEge.entity.dto.UserDto;
import com.kyraymege.StorEge.entity.dto.dtoRequest.EmailRequest;
import com.kyraymege.StorEge.entity.dto.dtoRequest.QrCodeRequest;
import com.kyraymege.StorEge.entity.dto.dtoRequest.ResetPasswordRequest;
import com.kyraymege.StorEge.entity.dto.dtoRequest.UserRequest;
import com.kyraymege.StorEge.entity.enums.TokenType;
import com.kyraymege.StorEge.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

import static java.util.Collections.emptyMap;

@RestController
@RequestMapping(path = {"/user"})
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;

    //////////////////////////// USER REGISTIRATION - START ////////////////////////////
    // USER REGISTRATION
    @PostMapping("/register")
    public ResponseEntity<Response> saveUser(@RequestBody @Valid UserRequest user, HttpServletRequest request) {
        userService.createUser(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword());
        return ResponseEntity.created(getUri()).body(RequestUtils.getResponse(request, emptyMap(), "Account Created. Check your email to activate your account.", HttpStatus.CREATED));
    }

    // USER EMAIL VERIFICATION
    @GetMapping("/verify/accountVerification")
    public ResponseEntity<Response> verifyAccount(@RequestParam("token") String token, HttpServletRequest request) {
        userService.verifyAccountToken(token);
        return ResponseEntity.ok().body(RequestUtils.getResponse(request, emptyMap(), "Account Verified", HttpStatus.OK));
    }
    //////////////////////////// USER REGISTIRATION - END ////////////////////////////

    //////////////////////////// MFA/2FA - START ////////////////////////////
    // MFA/2FA SETUP
    @PatchMapping("/mfa/setup")
    public ResponseEntity<Response> setupMfa(@AuthenticationPrincipal UserDto userPrincipal, HttpServletRequest request) {
        var user = userService.setupMfa(userPrincipal.getId());
        return ResponseEntity.ok().body(RequestUtils.getResponse(request, Map.of("user", user), "MFA set up success", HttpStatus.OK));
    }

    // MFA/2FA CANCEL
    @PatchMapping("/mfa/cancel")
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
    //////////////////////////// MFA/2FA - END ////////////////////////////

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

    //////////////////////////// RESET PASSWORD - END ////////////////////////////


    private URI getUri() {
        return URI.create("");
    }
}
