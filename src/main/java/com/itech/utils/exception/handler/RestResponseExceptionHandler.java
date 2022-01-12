package com.itech.utils.exception.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itech.utils.exception.EntityExistsException;
import com.itech.utils.exception.EntityNotFoundException;
import com.itech.utils.exception.IncorrectPasswordException;
import com.itech.utils.exception.ValidationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.log4j.Log4j2;
import org.iban4j.IbanFormatException;
import org.iban4j.InvalidCheckDigitException;
import org.iban4j.UnsupportedCountryException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Exception handler class.
 *
 * @author Edvard Krainiy on 12/13/2021
 */
@ControllerAdvice
@Log4j2
@PropertySource("classpath:properties/exception.properties")
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @Value("${custom.exception.authentication}")
    private String authenticationExceptionText;

    @Value("${custom.exception.expired.token}")
    private String expiredTokenExceptionText;

    @Value("${custom.exception.fetching}")
    private String fetchingExceptionText;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("MethodArgumentNotValidException was caught!");

        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            errors.add(errorMessage);
        });

        ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), errors);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    protected ResponseEntity<ApiError> handleException(RuntimeException ex) {
        log.error("IllegalArgumentException was caught!");

    ApiError exceptionError = new ApiError(HttpStatus.UNAUTHORIZED.value(), fetchingExceptionText);

        return new ResponseEntity<>(exceptionError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {ExpiredJwtException.class})
    protected ResponseEntity<ApiError> handleExpiredJwtException(RuntimeException ex) {
        log.error("ExpiredJwtException was caught!");

        ApiError exceptionError = new ApiError(HttpStatus.UNAUTHORIZED.value(), expiredTokenExceptionText);

        return new ResponseEntity<>(exceptionError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {SignatureException.class})
    protected ResponseEntity<ApiError> handleInvalidSignatureException(RuntimeException ex) {
        log.error("SignatureException was caught!");

        ApiError exceptionError = new ApiError(HttpStatus.UNAUTHORIZED.value(), authenticationExceptionText);

        return new ResponseEntity<>(exceptionError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {EntityExistsException.class})
    protected ResponseEntity<ApiError> handleUserExistsException(EntityExistsException ex) {
        log.error("EntityExistsException was caught!");

        ApiError exceptionError = new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());

        return new ResponseEntity<>(exceptionError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {IncorrectPasswordException.class})
    protected ResponseEntity<ApiError> handleIncorrectPasswordException(IncorrectPasswordException ex) {
        log.error("IncorrectPasswordException was caught!");

        ApiError exceptionError = new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());

        return new ResponseEntity<>(exceptionError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {NullPointerException.class})
    protected ResponseEntity<ApiError> handleNullPointerException(RuntimeException ex) {
        log.error("NullPointerException was caught!");

        ApiError exceptionError = new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());

        return new ResponseEntity<>(exceptionError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {EntityNotFoundException.class})
    protected ResponseEntity<ApiError> handleEntityNotFoundException(RuntimeException ex) {
        log.error("EntityNotFoundException was caught!");

        ApiError exceptionError = new ApiError(HttpStatus.NOT_FOUND.value(), ex.getMessage());

        return new ResponseEntity<>(exceptionError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {ValidationException.class})
    protected ResponseEntity<ApiError> handleValidationException(RuntimeException ex) {
        log.error("ValidationException was caught!");

        ApiError exceptionError = new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());

        return new ResponseEntity<>(exceptionError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {IbanFormatException.class, InvalidCheckDigitException.class, UnsupportedCountryException.class})
    protected ResponseEntity<ApiError> handleIbanExceptions(RuntimeException ex) {
        log.error("IbanException was caught!");

        ApiError exceptionError = new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());

        return new ResponseEntity<>(exceptionError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {JsonProcessingException.class})
    protected ResponseEntity<ApiError> handleJsonProcessingException(RuntimeException ex) {
        log.error("JsonProcessingException was caught!");

        ApiError exceptionError = new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());

        return new ResponseEntity<>(exceptionError, HttpStatus.BAD_REQUEST);
    }
}
