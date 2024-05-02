package com.kyraymege.StorEge.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kyraymege.StorEge.entity.Role;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.EAGER;

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
