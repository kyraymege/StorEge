package com.kyraymege.StorEge.business.concretes;

import com.kyraymege.StorEge.business.abstracts.EmailService;
import com.kyraymege.StorEge.exceptions.APIException;
import com.kyraymege.StorEge.utils.EmailUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.kyraymege.StorEge.utils.EmailUtils.getResetPasswordMessage;
import static com.kyraymege.StorEge.utils.EmailUtils.getVerificationEmailMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailManager implements EmailService {

    public static final String NEW_USER_ACCOUNT_VERIFICATION_SUBJECT = "New Account Verification";
    public static final String RESET_PASSWORD_SUBJECT = "Reset Password Request";
    private final JavaMailSender sender;

    @Value("${spring.mail.verify.host}")
    private String host;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    @Async
    public void sendNewAccountEmail(String name, String to, String token) {
        try {
            var mailMessage = new SimpleMailMessage();
            mailMessage.setSubject(NEW_USER_ACCOUNT_VERIFICATION_SUBJECT);
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(to);
            mailMessage.setText(getVerificationEmailMessage(name,host,token));
            sender.send(mailMessage);
        }catch (Exception e) {
            log.error(e.getMessage());
            throw new APIException("An error occurred while sending email");
        }
    }

    @Override
    @Async
    public void sendResetPasswordEmail(String name, String to, String token) {
        try {
            var mailMessage = new SimpleMailMessage();
            mailMessage.setSubject(RESET_PASSWORD_SUBJECT);
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(to);
            mailMessage.setText(getResetPasswordMessage(name,host,token));
            sender.send(mailMessage);
        }catch (Exception e) {
            log.error(e.getMessage());
            throw new APIException("An error occurred while sending email");
        }
    }
}
