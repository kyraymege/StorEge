package com.kyraymege.StorEge.entity.dto.dtoRequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class QrCodeRequest {
    @NotEmpty(message = "User Id is required")
    private String userId;
    @NotEmpty(message = "QR Code is required")
    private String qrCode;
}
