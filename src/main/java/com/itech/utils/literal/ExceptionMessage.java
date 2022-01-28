package com.itech.utils.literal;

/**
 * ExceptionMessage class. Contains all necessary messages of exceptions.
 *
 * @author Edvard Krainiy on 01/25/2022
 */
public class ExceptionMessage {
  public static final String ACCOUNT_NOT_FOUND = "Account not found!";
  public static final String ACCOUNT_CREATION_REQUEST_NOT_FOUND =
      "Account CreationRequest with this id not found!";
  public static final String ACCOUNTS_ARE_EMPTY = "Accounts are empty!";
  public static final String AUTHENTICATED_USER_NOT_FOUND = "Authenticated user not found!";
  public static final String ID_OF_LOGGED_USER_NOT_EQUALS_ID_OF_ACCOUNT =
      "Id of this account is not equals id of logged user!";
  public static final String USER_NOT_FOUND = "User not found!";
  public static final String USER_NOT_ACTIVATED = "This user not activated!";
  public static final String USER_IS_ALREADY_EXISTS = "This user is already exists!";
  public static final String USER_IS_ALREADY_ACTIVATED = "This user is already activated!";
  public static final String TRANSACTION_CREATION_REQUEST_NOT_FOUND =
      "Transaction CreationRequest with this id not found!";
  public static final String TRANSACTION_NOT_FOUND = "Transaction not found!";
  public static final String CREATION_REQUEST_EXPIRED = "Time of transaction is over!";
  public static final String INCORRECT_REQUEST_STRUCTURE =
      "Incorrect structure of request. It must be at least 1 DEBIT and 1 CREDIT operations, and sum of CREDIT minus sum of DEBIT operation amounts must equals 0.";
  public static final String OPERATIONS_ARE_EMPTY = "Operations are empty!";
  public static final String CURRENCIES_ARE_NOT_SAME = "Currencies aren't same!";
  public static final String CREDIT_IS_MORE_THAN_STORED_ON_ACCOUNT =
      "CREDIT amount is more than stored in this account.";

  private ExceptionMessage() {}
}
