package com.kyraymege.StorEge.entity.dto.dtoRequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdatePasswordRequest {
    @NotEmpty(message = "Password is required")
    private String password;
    @NotEmpty(message = "New Password is required")
    private String newPassword;
    @NotEmpty(message = "Confirm New Password is required")
    private String confirmNewPassword;
}