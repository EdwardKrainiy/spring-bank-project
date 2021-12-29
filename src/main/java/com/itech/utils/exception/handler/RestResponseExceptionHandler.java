package com.itech.utils.exception.handler;

import com.itech.utils.exception.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.iban4j.IbanFormatException;
import org.iban4j.InvalidCheckDigitException;
import org.iban4j.UnsupportedCountryException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.*;

/**
 * Exception handler class.
 *
 * @author Edvard Krainiy on 12/13/2021
 */
@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            errors.add(errorMessage);
        });

        ApiError error = new ApiError("400", errors);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    protected ResponseEntity<ApiError> handleException(RuntimeException ex) {
        ApiError exceptionError = new ApiError("401", "An error occurred while fetching Username from Token");
        return new ResponseEntity<>(exceptionError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {ExpiredJwtException.class})
    protected ResponseEntity<ApiError> handleExpiredJwtException(RuntimeException ex) {
        ApiError exceptionError = new ApiError("401", "The token has expired!");
        return new ResponseEntity<>(exceptionError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {SignatureException.class})
    protected ResponseEntity<ApiError> handleInvalidSignatureException(RuntimeException ex) {
        ApiError exceptionError = new ApiError("401", "Authentication Failed. Username or Password is not valid.");
        return new ResponseEntity<>(exceptionError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {EntityExistsException.class})
    protected ResponseEntity<ApiError> handleUserExistsException(EntityExistsException ex) {
        ApiError exceptionError = new ApiError("400", ex.getMessage());
        return new ResponseEntity<>(exceptionError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {IncorrectPasswordException.class})
    protected ResponseEntity<ApiError> handleIncorrectPasswordException(IncorrectPasswordException ex) {
        ApiError exceptionError = new ApiError("400", ex.getMessage());
        return new ResponseEntity<>(exceptionError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {NullPointerException.class})
    protected ResponseEntity<ApiError> handleNullPointerException(RuntimeException ex) {
        ApiError exceptionError = new ApiError("400", ex.getMessage());
        return new ResponseEntity<>(exceptionError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {EntityNotFoundException.class})
    protected ResponseEntity<ApiError> handleEntityNotFoundException(RuntimeException ex) {
        ApiError exceptionError = new ApiError("404", ex.getMessage());
        return new ResponseEntity<>(exceptionError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {ValidationException.class})
    protected ResponseEntity<ApiError> handleValidationException(RuntimeException ex) {
        ApiError exceptionError = new ApiError("400", ex.getMessage());
        return new ResponseEntity<>(exceptionError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {IbanFormatException.class, InvalidCheckDigitException.class, UnsupportedCountryException.class})
    protected ResponseEntity<ApiError> handleIbanExceptions(RuntimeException ex) {
        ApiError exceptionError = new ApiError("400", ex.getMessage());
        return new ResponseEntity<>(exceptionError, HttpStatus.BAD_REQUEST);
    }
}
