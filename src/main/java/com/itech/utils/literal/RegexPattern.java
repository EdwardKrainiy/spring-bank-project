package com.itech.utils.literal;

public class RegexPattern {
    private RegexPattern(){
    }
    public static final String VALID_USERNAME_ADDRESS_REGEX = "^[a-zA-Z0-9._-]{3,}$";
    public static final String VALID_EMAIL_ADDRESS_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
}
