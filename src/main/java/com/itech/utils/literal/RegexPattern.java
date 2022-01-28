package com.itech.utils.literal;

/**
 * RegexPattern class. Contains all necessary regex patterns for validation.
 *
 * @author Edvard Krainiy on 01/25/2022
 */
public class RegexPattern {
  public static final String VALID_USERNAME_ADDRESS_REGEX = "^[a-zA-Z0-9._-]{3,}$";
  public static final String VALID_EMAIL_ADDRESS_REGEX =
      "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

  private RegexPattern() {}
}
