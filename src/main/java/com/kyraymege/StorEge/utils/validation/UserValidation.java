package com.kyraymege.StorEge.utils.validation;

import com.kyraymege.StorEge.entity.concretes.User;
import com.kyraymege.StorEge.exceptions.APIException;

public class UserValidation {

    public static void verifyAccountStatus(User user){
        if(!user.isEnabled()){
            throw new APIException("User account is disabled");
        }
        if(!user.isAccountNonExpired()){
            throw new APIException("User account is expired");
        }
        if(!user.isAccountNonLocked()){
            throw new APIException("User account is locked");
        }
    }
}
