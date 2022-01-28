package com.itech.utils.literal;

/**
 * PropertySourceClasspath class. Contains all necessary classpaths of .property files.
 *
 * @author Edvard Krainiy on 01/25/2022
 */
public class PropertySourceClasspath {
  public static final String SCHEDULER_PROPERTIES_CLASSPATH =
      "classpath:properties/scheduler.properties";
  public static final String MAIL_PROPERTIES_CLASSPATH = "classpath:properties/mail.properties";
  public static final String SECURITY_PROPERTIES_CLASSPATH =
      "classpath:properties/security.properties";
  public static final String JWT_PROPERTIES_CLASSPATH = "classpath:properties/jwt.properties";
  public static final String CREATE_USERS_SQL_CLASSPATH = "classpath:db/test/create_users.sql";
  public static final String CREATE_ACCOUNTS_SQL_CLASSPATH =
      "classpath:db/test/create_accounts.sql";
  public static final String CREATE_TRANSACTIONS_SQL_CLASSPATH =
      "classpath:db/test/create_transactions.sql";
  public static final String APPLICATION_PROPERTIES_CLASSPATH = "classpath:application.properties";
  public static final String APPLICATION_TEST_PROPERTIES_CLASSPATH =
      "classpath:application-integrationtest.properties";

  private PropertySourceClasspath() {}
}
