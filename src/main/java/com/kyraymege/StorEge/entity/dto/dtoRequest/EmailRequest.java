package com.kyraymege.StorEge.entity.dto.dtoRequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailRequest {
    @Email(message = "Email should be valid")
    @NotEmpty(message = "Email is required")
    private String email;
}

