package com.itech.utils.literal;

/**
 * LogMessage class. Contains all necessary messages of logs.
 *
 * @author Edvard Krainiy on 01/25/2022
 */
public class LogMessage {
  public static final String METHOD_ARGUMENT_NOT_VALID_LOG =
      "MethodArgumentNotValidException was caught and successfully handled. Name of object: %s. List of all validation errors: %s";
  public static final String DEBUG_REQUEST_BODY_LOG = "Request body: {%s}";
  public static final String AUTHENTICATED_USER_NOT_FOUND_LOG = "Authenticated user not found.";
  public static final String ACCOUNT_NOT_FOUND_LOG =
      "Account not found. Id of requested account: %d";
  public static final String ACCOUNT_WITH_NUMBER_NOT_FOUND_LOG =
      "Account not found. Number of requested account: %s";
  public static final String ACCOUNT_CREATION_REQUEST_CREATED_LOG =
      "Account creation request created. Id of Creation Request: %d";
  public static final String TRANSACTION_CREATION_REQUEST_CREATED_LOG =
      "Transaction creation request created. Id of Creation Request: %d";
  public static final String ACCOUNT_CREATION_REQUEST_UPDATED_LOG =
      "Account creation request updated. Id of Creation Request: %d";
  public static final String EMAIL_NOT_FOUND_LOG =
      "Email of User not found, and mail not sent, but request was processed. Id of user: %d";
  public static final String ID_OF_LOGGED_USER_NOT_EQUALS_ID_OF_ACCOUNT_LOG =
      "Id of logged user not equals id of account. Id of logged User: %d, Id of account user: %d";
  public static final String ACCOUNT_UPDATED_LOG = "Account was updated. Id of account: %d";
  public static final String ACCOUNT_DELETED_LOG = "Account was deleted. Id of account: %d";
  public static final String ACCOUNT_CREATION_REQUEST_NOT_FOUND_LOG =
      "Account creation request not found. Id of requested account creation request: %d";
  public static final String TRANSACTION_NOT_FOUND_LOG =
      "Transaction not found. Id of requested transaction: %d";
  public static final String ACCOUNT_CREATED_LOG = "Account was created. Id of created account: %d";
  public static final String MESSAGE_SENT_LOG = "Message was successfully sent to email: %s";
  public static final String MESSAGE_SENT_TO_QUEUE_LOG = "Message was successfully sent to queue.";
  public static final String USER_NOT_FOUND_LOG = "User not found. Id of requested user: %d";
  public static final String TRANSACTION_REJECTED_LOG =
      "Transaction was rejected. Id of rejected transaction: %d";
  public static final String TRANSACTION_CREATED_LOG =
      "Transaction was created successfully! Id of created transaction: %d";
  public static final String TRANSACTION_CREATION_REQUEST_NOT_FOUND_LOG =
      "Transaction creation request not found. Id of requested transaction creation request: %d";
  public static final String CREDIT_IS_MORE_THAN_STORED_ON_ACCOUNT_LOG =
      "Transaction creation request was rejected. CREDIT amount is more than stored in this account. Id of account, which not have enough amount: %d";
  public static final String TRANSACTION_CREATION_REQUEST_EXPIRED_LOG =
      "Transaction creation request expired. Id of request %d";
  public static final String USER_IS_ALREADY_EXISTS_LOG = "User is already exists.";
  public static final String MANAGER_USER_NOT_EXISTS_LOG =
      "Manager user not exists, and confirmation message cannot be sent.";
  public static final String USER_AUTHENTICATED_LOG =
      "User was authenticated. Username: %s Password: %s Token: %s";
  public static final String OPERATIONS_ARE_EMPTY_LOG = "Operations are empty!";

  private LogMessage() {}
}
