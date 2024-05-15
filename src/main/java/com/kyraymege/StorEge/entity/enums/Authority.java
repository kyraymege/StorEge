package com.kyraymege.StorEge.entity.enums;

import static com.kyraymege.StorEge.utils.consts.Constants.ADMIN_AUTHORITIES;
import static com.kyraymege.StorEge.utils.consts.Constants.USER_AUTHORITIES;

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
