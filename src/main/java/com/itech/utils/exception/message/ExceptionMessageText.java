package com.itech.utils.exception.message;

public class ExceptionMessageText {
    private ExceptionMessageText(){
    }
    public static final String ACCOUNT_NOT_FOUND = "Account not found!";
    public static final String ACCOUNT_CREATION_REQUEST_WITH_ID_NOT_FOUND = "Account CreationRequest with this id not found!";
    public static final String ACCOUNT_CREATION_REQUESTS_NOT_FOUND = "Account CreationRequest not found!";
    public static final String ACCOUNTS_ARE_EMPTY = "Accounts are empty!";

    public static final String AUTHENTICATED_USER_NOT_FOUND = "Authenticated user not found!";
    public static final String ID_OF_LOGGED_USER_NOT_EQUALS_ID_OF_ACCOUNT = "Id of this account is not equals id of logged user!";
    public static final String LOGGED_USER_NOT_FOUND = "Logged user not found!";
    public static final String USER_EMAIL_NOT_FOUND = "User email is not found!";
    public static final String USER_NOT_FOUND = "User not found!";
    public static final String USER_NOT_ACTIVATED = "This user not activated!";
    public static final String USER_IS_ALREADY_EXISTS = "This user is already exists!";
    public static final String USER_IS_ALREADY_ACTIVATED = "This user is already activated!";

    public static final String TRANSACTION_CREATION_REQUESTS_NOT_FOUND = "Transaction not found!";
    public static final String TRANSACTION_CREATION_REQUEST_WITH_ID_NOT_FOUND = "Transaction CreationRequest with this id not found!";

    public static final String CREATION_REQUEST_NOT_FOUND = "CreationRequest not found!";
    public static final String CREATION_REQUEST_EXPIRED = "Time of transaction is over!";
    public static final String INCORRECT_REQUEST_STRUCTURE = "Incorrect structure of request. It must be at least 1 DEBIT and 1 CREDIT operations, and sum of CREDIT minus sum of DEBIT operation amounts must equals 0.";

    public static final String OPERATIONS_ARE_EMPTY = "Operations are empty!";
    public static final String CURRENCIES_ARE_NOT_SAME = "Currencies aren't same!";
    public static final String CREDIT_IS_MORE_THAN_STORED_ON_ACCOUNT = "CREDIT amount is more than stored in this account.";

    public static final String AUTHENTICATION_FAILED = "Authentication Failed. Username or Password is not valid.";
    public static final String TOKEN_IS_EXPIRED = "The token has expired!";
    public static final String FETCHING_EXCEPTION = "An error occurred while fetching Username from Token.";
}
