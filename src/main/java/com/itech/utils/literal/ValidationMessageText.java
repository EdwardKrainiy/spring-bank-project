package com.itech.utils.literal;

public class ValidationMessageText {
  public static final String INCORRECT_CURRENCY_EXCEPTION_MESSAGE = "Incorrect Currency!";
  public static final String AMOUNT_MUST_BE_GREATER_THAN_ZERO_EXCEPTION_MESSAGE =
      "Amount must be greater than 0!";
  public static final String ACCOUNT_NUMBER_IS_EMPTY_EXCEPTION_MESSAGE = "Account number is empty!";
  public static final String INCORRECT_OPERATION_TYPE_EXCEPTION_MESSAGE =
      "Incorrect OperationType!";
  public static final String EMPTY_OPERATIONS_EXCEPTION_MESSAGE = "Operations are empty!";
  public static final String MINIMAL_OPERATIONS_SIZE_EXCEPTION_MESSAGE =
      "Minimal size of operations is 2!";
  public static final String INCORRECT_STRUCTURE_OF_OPERATIONS_EXCEPTION_MESSAGE =
      "Operations CREDIT and DEBIT cannot be applied on the same account, must be at least 1 CREDIT and 1 DEBIT operation.";
  public static final String USERNAME_IS_NOT_VALID_EXCEPTION_MESSAGE = "Username is not valid!";
  public static final String USERNAME_IS_EMPTY_EXCEPTION_MESSAGE = "Username is empty!";
  public static final String PASSWORD_IS_EMPTY_MESSAGE_TEXT = "Password is empty!";
  public static final String INCORRECT_PASSWORD_LENGTH_MESSAGE_TEXT =
      "Incorrect password length! It must be from 5 to 20.";
  public static final String EMAIL_IS_NOT_VALID_EXCEPTION_MESSAGE_TEXT = "Email is not valid!";
  public static final String EMAIL_IS_EMPTY_MESSAGE_TEXT = "Email is empty!";

  private ValidationMessageText() {}
}
