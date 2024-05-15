package com.kyraymege.StorEge.entity.dto.dtoRequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResetPasswordRequest {
    @NotEmpty(message = "User ID is required")
    private String userId;
    @NotEmpty(message = "New Password is required")
    private String newPassword;
    @NotEmpty(message = "Confirm Password is required")
    private String confirmNewPassword;
}
