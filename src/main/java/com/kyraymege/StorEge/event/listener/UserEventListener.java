package com.kyraymege.StorEge.event.listener;

import com.kyraymege.StorEge.business.abstracts.EmailService;
import com.kyraymege.StorEge.event.UserEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventListener {
    private final EmailService emailService;

    @EventListener
    public void onUserEvent(UserEvent event){
        switch (event.getEventType()){
            case REGISTIRATION:
                emailService.sendNewAccountEmail(event.getUser().getFirstName(),event.getUser().getEmail(),(String) event.getData().get("key"));
                break;
            case RESETPASSWORD:
                emailService.sendResetPasswordEmail(event.getUser().getFirstName(),event.getUser().getEmail(),(String) event.getData().get("key"));
                break;
            default:
                break;
        }

    }
}
