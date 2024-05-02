package com.kyraymege.StorEge.enums;

import static com.kyraymege.StorEge.consts.Constants.ADMIN_AUTHORITIES;
import static com.kyraymege.StorEge.consts.Constants.USER_AUTHORITIES;

public enum Authority {
    USER(USER_AUTHORITIES),
    ADMIN(ADMIN_AUTHORITIES);
    private final String value;

    Authority(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
