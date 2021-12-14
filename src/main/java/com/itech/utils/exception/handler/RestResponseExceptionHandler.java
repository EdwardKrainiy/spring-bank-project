package com.itech.utils.exception.handler;

import com.itech.utils.exception.IncorrectPasswordException;
import com.itech.utils.exception.UserExistsException;
import com.itech.utils.exception.UserNotFoundException;
import com.itech.utils.exception.UserValidationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Exception handler class.
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
    protected ResponseEntity<ApiError> handleSignatureException(RuntimeException ex){
        ApiError exceptionError = new ApiError("Authentication Failed. Username or Password is not valid.", ex.getMessage());
        return new ResponseEntity<>(exceptionError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {UserNotFoundException.class})
    protected ResponseEntity<ApiError> handleUserNotFoundException(RuntimeException ex){
        ApiError exceptionError = new ApiError(ex.getMessage());
        return new ResponseEntity<>(exceptionError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {UserValidationException.class})
    protected ResponseEntity<ApiError> handleUserValidationException(RuntimeException ex){
        ApiError exceptionError = new ApiError(ex.getMessage());
        return new ResponseEntity<>(exceptionError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {UserExistsException.class})
    protected ResponseEntity<ApiError> handleUserExistsException(RuntimeException ex){
        ApiError exceptionError = new ApiError(ex.getMessage());
        return new ResponseEntity<>(exceptionError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {IncorrectPasswordException.class})
    protected ResponseEntity<ApiError> handleIncorrectPasswordException(RuntimeException ex){
        ApiError exceptionError = new ApiError(ex.getMessage());
        return new ResponseEntity<>(exceptionError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {NullPointerException.class})
    protected ResponseEntity<ApiError> handleNullPointerException(RuntimeException ex){
        ApiError exceptionError = new ApiError(ex.getMessage());
        return new ResponseEntity<>(exceptionError, HttpStatus.BAD_REQUEST);
    }
}
