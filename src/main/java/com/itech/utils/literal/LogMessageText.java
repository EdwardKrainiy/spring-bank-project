package com.itech.utils.literal;

public class LogMessageText {
    private LogMessageText(){
    }
    public static final String METHOD_ARGUMENT_NOT_VALID_LOG = "MethodArgumentNotValidException was caught and successfully handled. Name of object: %s. List of all validation errors: %s";
    public static final String DEBUG_REQUEST_BODY_LOG = "Request body: {%s}";
    public static final String AUTHENTICATED_USER_NOT_FOUND_LOG = "Authenticated user not found.";
    public static final String ACCOUNT_WITH_ID_NOT_FOUND_LOG = "Account not found. Id of requested account: %d";
    public static final String ACCOUNT_CREATION_REQUEST_CREATED_LOG = "Account creation request created. Id of Creation Request: %d";
    public static final String EMAIL_NOT_FOUND_LOG = "Email of User not found, and mail not sent, but request was processed. Id of user: %d";
    public static final String ID_OF_LOGGED_USER_NOT_EQUALS_ID_OF_ACCOUNT_LOG = "Id of logged user not equals id of account. Id of logged User: %d, Id of account user: %d";
}
