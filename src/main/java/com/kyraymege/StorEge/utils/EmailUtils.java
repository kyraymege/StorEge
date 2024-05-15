package com.kyraymege.StorEge.utils;

public class EmailUtils {

    public static String getVerificationEmailMessage(String name,String host,String token){
        return "Hello "+name+",\n\n"+
                "Thank you for registering with us. Please click on the below link to activate your account:\n\n"+
                getVerificationUrl(host,token) +
                "\n\nThank you\nThe StorEge Team";
    }

    public static String getResetPasswordMessage(String name,String host,String token){
        return "Hello "+name+",\n\n"+
                "Here's the reset password link :\n\n"+
                getResetPasswordUrl(host,token) +
                "\n\nThank you\nThe StorEge Team";
    }




    public static String getVerificationUrl(String host,String token){
        return host+"/user/verify/accountVerification?token="+token;
    }

    public static String getResetPasswordUrl(String host,String token){
        return host+"/user/verify/password?token="+token;
    }
}
