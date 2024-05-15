package com.kyraymege.StorEge.entity.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private Long createdBy;
    private Long updatedBy;
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String bio;
    private String profilePicture;
    private String qrCodeSecret;
    private String qrCodeImageUri;
    private String lastLogin;
    private String createdAt;
    private String updatedAt;
    private String role;
    private String authorities;
    private Integer loginAttempts;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;
    private boolean mfa;
}
