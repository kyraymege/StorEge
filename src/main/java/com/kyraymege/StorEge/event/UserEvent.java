package com.kyraymege.StorEge.event;

import com.kyraymege.StorEge.entity.concretes.User;
import com.kyraymege.StorEge.entity.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class UserEvent {
    private User user;
    private EventType eventType;
    private Map<?,?> data;
}
