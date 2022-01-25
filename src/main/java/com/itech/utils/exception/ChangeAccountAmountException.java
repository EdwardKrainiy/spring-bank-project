package com.itech.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ChangeAccountAmountException class.
 *
 * @author Edvard Krainiy on 01/05/2022
 */

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ChangeAccountAmountException extends Exception {
  public ChangeAccountAmountException(String message) {
    super(message);
  }
}
