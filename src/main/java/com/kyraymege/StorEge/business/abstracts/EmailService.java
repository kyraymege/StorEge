package com.kyraymege.StorEge.business.abstracts;

public interface EmailService {
    void sendNewAccountEmail(String name, String to, String token);
    void sendResetPasswordEmail(String name, String to, String token);
}
