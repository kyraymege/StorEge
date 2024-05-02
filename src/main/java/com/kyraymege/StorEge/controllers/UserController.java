package com.kyraymege.StorEge.controllers;

import com.kyraymege.StorEge.business.abstracts.UserService;
import com.kyraymege.StorEge.domain.Response;
import com.kyraymege.StorEge.dtoRequest.UserRequest;
import com.kyraymege.StorEge.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static java.util.Collections.emptyMap;

@RestController
@RequestMapping(path = {"/user"})
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

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


    private URI getUri() {
        return URI.create("");
    }
}
