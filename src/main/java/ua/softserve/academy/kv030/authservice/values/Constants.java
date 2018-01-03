package ua.softserve.academy.kv030.authservice.values;

import io.jsonwebtoken.SignatureAlgorithm;

public class Constants {

    // Password lifetime in days
    public static final int PASSWORD_VALIDITY_DAYS = 60;

    // Password expiry notice occurs 7 days before the password expiration date
    public static final int PASSWORD_EXPIRY_NOTICE_DAYS = 7;


    public static final String TOKEN_HEADER = "X-AUTH";
    public static final SignatureAlgorithm jwtSignatureAlgorithm = SignatureAlgorithm.HS512;

    public static final String fileServiceFileEndpoint = "files/";
    public static final String authServiceFileEndpoint = "files/";


    public static final int KEY_SIZE = 16;

}
