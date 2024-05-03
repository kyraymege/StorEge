package com.kyraymege.StorEge.controllers;

import com.kyraymege.StorEge.business.abstracts.JwtService;
import com.kyraymege.StorEge.business.abstracts.UserService;
import com.kyraymege.StorEge.domain.Response;
import com.kyraymege.StorEge.dto.UserDto;
import com.kyraymege.StorEge.dtoRequest.QrCodeRequest;
import com.kyraymege.StorEge.dtoRequest.UserRequest;
import com.kyraymege.StorEge.enums.TokenType;
import com.kyraymege.StorEge.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<Response> saveUser(@RequestBody @Valid UserRequest user, HttpServletRequest request) {
        userService.createUser(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword());
        return ResponseEntity.created(getUri()).body(RequestUtils.getResponse(request, emptyMap(), "Account Created. Check your email to activate your account.", HttpStatus.CREATED));
    }

    @GetMapping("/verify/accountVerification")
    public ResponseEntity<Response> verifyAccount(@RequestParam("token") String token, HttpServletRequest request) {
        userService.verifyAccountToken(token);
        return ResponseEntity.ok().body(RequestUtils.getResponse(request, emptyMap(), "Account Verified", HttpStatus.OK));
    }

    @PatchMapping("/mfa/setup")
    public ResponseEntity<Response> setupMfa(@AuthenticationPrincipal UserDto userPrincipal, HttpServletRequest request) {
        var user = userService.setupMfa(userPrincipal.getId());
        return ResponseEntity.ok().body(RequestUtils.getResponse(request, Map.of("user", user), "MFA set up success", HttpStatus.OK));
    }

    @PatchMapping("/mfa/cancel")
    public ResponseEntity<Response> cancelMfa(@AuthenticationPrincipal UserDto userPrincipal, HttpServletRequest request) {
        var user = userService.cancelMfa(userPrincipal.getId());
        return ResponseEntity.ok().body(RequestUtils.getResponse(request, Map.of("user", user), "MFA canceled successfully", HttpStatus.OK));
    }

    @PostMapping("/verify/qrcode")
    public ResponseEntity<Response> verifyQrCode(@RequestBody QrCodeRequest qrCodeRequest, HttpServletResponse response, HttpServletRequest request) {
        var user = userService.verifyQrCode(qrCodeRequest.getUserId(), qrCodeRequest.getQrCode());
        jwtService.addCookie(response, user, TokenType.ACCESS);
        jwtService.addCookie(response, user, TokenType.REFRESH);
        return ResponseEntity.ok().body(RequestUtils.getResponse(request, Map.of("user", user), "Qr Code Verified", HttpStatus.OK));
    }


    private URI getUri() {
        return URI.create("");
    }
}
