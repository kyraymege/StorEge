package com.kyraymege.StorEge.utils.consts;

public class Constants {
    public static final String BASE_PATH = "/**";
    public static final String FILE_NAME = "File-Name";
    public static final int CRED_EXPIRED_DAYS = 90;
    public static final String[] PUBLIC_ROUTES = {
            "/user/login",
            "/user/update",
            "/user/id",
            "/user/register",
            "/user/new/password",
            "/user/refresh/token",
            "/user/stream",
            "/user/verify/accountVerification",
            "/user/verify/password",
            "/user/verify/qrcode",
            "/user/login",
            "/user/resetpassword",
            "/user/resetpassword/reset",
            "/user/profile",
            "/user/toogleAccountExpired",
            "/user/toogleAccountLocked",
            "/user/toogleAccountEnabled",
            "/user/toogleCredentialsExpired",
            "/user/updateRole",
            "user/updatePassword",
            "/user/photo",
            "/user/image/**",
    };
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String AUTHORITIES = "authorities";
    public static final String ROLE = "role";
    public static final String AUTHORITY_DELIMITER = ",";
    public static final String STOREGE = "STOREGE";
    public static final String EMPTY_VALUE = "empty";
    public static final String USER_AUTHORITIES = "document:create,document:read,document:update,document:delete";
    public static final String ADMIN_AUTHORITIES = "user:create,user:read,user:update,user:delete,document:create,document:read,document:update,document:delete";
    public static final String PHOTO_DIRECTORY = System.getProperty("user.home")+"Downloads/uploads/";

}
