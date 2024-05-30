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
            "/documents",
            "/documents/search",
            "/documents/download/**",
            "/documents/**",
            "/documents/upload",
    };
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String AUTHORITIES = "authorities";
    public static final String ROLE = "role";
    public static final String AUTHORITY_DELIMITER = ",";
    public static final String STOREGE = "STOREGE";
    public static final String EMPTY_VALUE = "empty";
    public static final String USER_AUTHORITIES = "document:create,document:read,document:update,document:delete";
    public static final String ADMIN_AUTHORITIES = "user:create,user:read,user:update,user:delete,document:create,document:read,document:update,document:delete";
    public static final String FILE_DIRECTORY = System.getProperty("user.home")+"Downloads/uploads/";

    // Document Constants
    public static final String SELECT_DOCUMENTS_QUERY = "SELECT doc.id, doc.name, doc.document_id, doc.description, doc.uri, doc.icon, doc.size, doc.formatted_size, doc.extension, doc.reference_id,doc.created_at,doc.updated_at, CONCAT(owner.first_name, ' ', owner.last_name) AS owner_name, owner.email AS owner_email, owner.phone AS owner_phone, owner.last_login AS owner_last_login, CONCAT(updater.first_name, ' ', updater.last_name) AS updater_name FROM documents doc JOIN users owner ON owner.id = doc.created_by JOIN users updater ON updater.id = doc.updated_by";
    public static final String SELECT_DOCUMENT_QUERY = "SELECT doc.id, doc.name, doc.document_id, doc.description, doc.uri, doc.icon, doc.size, doc.formatted_size, doc.extension, doc.reference_id,doc.created_at,doc.updated_at, CONCAT(owner.first_name, ' ', owner.last_name) AS owner_name, owner.email AS owner_email, owner.phone AS owner_phone, owner.last_login AS owner_last_login, CONCAT(updater.first_name, ' ', updater.last_name) AS updater_name FROM documents doc JOIN users owner ON owner.id = doc.created_by JOIN users updater ON updater.id = doc.updated_by WHERE doc.document_id = ?1";
    public static final String SELECT_DOCUMENTS_BYNAME_QUERY = "SELECT doc.id, doc.name, doc.document_id, doc.description, doc.uri, doc.icon, doc.size, doc.formatted_size, doc.extension, doc.reference_id,doc.created_at,doc.updated_at, CONCAT(owner.first_name, ' ', owner.last_name) AS owner_name, owner.email AS owner_email, owner.phone AS owner_phone, owner.last_login AS owner_last_login, CONCAT(updater.first_name, ' ', updater.last_name) AS updater_name FROM documents doc JOIN users owner ON owner.id = doc.created_by JOIN users updater ON updater.id = doc.updated_by WHERE doc.name ~* :documentName";
}
