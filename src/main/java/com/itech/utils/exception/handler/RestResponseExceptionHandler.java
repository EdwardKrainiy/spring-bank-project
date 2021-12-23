package com.itech.utils.exception.handler;

import com.itech.utils.exception.account.AccountNotFoundException;
import com.itech.utils.exception.account.AccountValidationException;
import com.itech.utils.exception.transaction.TransactionNotFoundException;
import com.itech.utils.exception.user.IncorrectPasswordException;
import com.itech.utils.exception.user.UserExistsException;
import com.itech.utils.exception.user.UserNotFoundException;
import com.itech.utils.exception.user.UserValidationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.iban4j.IbanFormatException;
import org.iban4j.InvalidCheckDigitException;
import org.iban4j.UnsupportedCountryException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Exception handler class.
 *
 * @author Edvard Krainiy on 12/13/2021
 */
@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {IllegalArgumentException.class})
    protected ResponseEntity<ApiError> handleIllegalArgumentException(RuntimeException ex) {
        ApiError exceptionError = new ApiError("An error occurred while fetching Username from Token", ex.getMessage());
        return new ResponseEntity<>(exceptionError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {ExpiredJwtException.class})
    protected ResponseEntity<ApiError> handleExpiredJwtException(RuntimeException ex) {
        ApiError exceptionError = new ApiError("The token has expired!", ex.getMessage());
        return new ResponseEntity<>(exceptionError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {SignatureException.class})
    protected ResponseEntity<ApiError> handleInvalidSignatureException(RuntimeException ex) {
        ApiError exceptionError = new ApiError("Authentication Failed. Username or Password is not valid.", ex.getMessage());
        return new ResponseEntity<>(exceptionError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {UserExistsException.class})
    protected ResponseEntity<ApiError> handleUserExistsException(UserExistsException ex) {
        ApiError exceptionError = new ApiError(ex.getMessage());
        return new ResponseEntity<>(exceptionError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {IncorrectPasswordException.class})
    protected ResponseEntity<ApiError> handleIncorrectPasswordException(IncorrectPasswordException ex) {
        ApiError exceptionError = new ApiError(ex.getMessage());
        return new ResponseEntity<>(exceptionError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {NullPointerException.class})
    protected ResponseEntity<ApiError> handleNullPointerException(RuntimeException ex) {
        ApiError exceptionError = new ApiError(ex.getMessage());
        return new ResponseEntity<>(exceptionError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {AccountNotFoundException.class, TransactionNotFoundException.class, UserNotFoundException.class})
    protected ResponseEntity<ApiError> handleEntityNotFoundException(RuntimeException ex) {
        ApiError exceptionError = new ApiError(ex.getMessage());
        return new ResponseEntity<>(exceptionError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {AccountValidationException.class, UserValidationException.class})
    protected ResponseEntity<ApiError> handleEntityValidationException(RuntimeException ex) {
        ApiError exceptionError = new ApiError(ex.getMessage());
        return new ResponseEntity<>(exceptionError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {IbanFormatException.class, InvalidCheckDigitException.class, UnsupportedCountryException.class})
    protected ResponseEntity<ApiError> handleIbanExceptions(RuntimeException ex) {
        ApiError exceptionError = new ApiError(ex.getMessage());
        return new ResponseEntity<>(exceptionError, HttpStatus.BAD_REQUEST);
    }
}
