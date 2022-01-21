package com.itech.utils.exception.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itech.model.dto.exception.ApiErrorDto;
import com.itech.utils.exception.EntityExistsException;
import com.itech.utils.exception.EntityNotFoundException;
import com.itech.utils.exception.IncorrectPasswordException;
import com.itech.utils.exception.ValidationException;
import com.itech.utils.exception.message.ExceptionMessageText;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.log4j.Log4j2;
import org.iban4j.IbanFormatException;
import org.iban4j.InvalidCheckDigitException;
import org.iban4j.UnsupportedCountryException;
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
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("MethodArgumentNotValidException was caught!");

        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            errors.add(errorMessage);
        });

        ApiErrorDto error = new ApiErrorDto(HttpStatus.BAD_REQUEST.value(), errors);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    protected ResponseEntity<ApiErrorDto> handleException(RuntimeException ex) {
        log.error("IllegalArgumentException was caught!");

        ApiErrorDto exceptionError = new ApiErrorDto(HttpStatus.UNAUTHORIZED.value(), ExceptionMessageText.FETCHING_EXCEPTION);

        return new ResponseEntity<>(exceptionError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {ExpiredJwtException.class})
    protected ResponseEntity<ApiErrorDto> handleExpiredJwtException(RuntimeException ex) {
        log.error("ExpiredJwtException was caught!");

        ApiErrorDto exceptionError = new ApiErrorDto(HttpStatus.UNAUTHORIZED.value(), ExceptionMessageText.TOKEN_IS_EXPIRED);

        return new ResponseEntity<>(exceptionError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {SignatureException.class})
    protected ResponseEntity<ApiErrorDto> handleInvalidSignatureException(RuntimeException ex) {
        log.error("SignatureException was caught!");

        ApiErrorDto exceptionError = new ApiErrorDto(HttpStatus.UNAUTHORIZED.value(), ExceptionMessageText.AUTHENTICATION_FAILED);

        return new ResponseEntity<>(exceptionError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {EntityExistsException.class})
    protected ResponseEntity<ApiErrorDto> handleUserExistsException(EntityExistsException ex) {
        log.error("EntityExistsException was caught!");

        ApiErrorDto exceptionError = new ApiErrorDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage());

        return new ResponseEntity<>(exceptionError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {IncorrectPasswordException.class})
    protected ResponseEntity<ApiErrorDto> handleIncorrectPasswordException(IncorrectPasswordException ex) {
        log.error("IncorrectPasswordException was caught!");

        ApiErrorDto exceptionError = new ApiErrorDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage());

        return new ResponseEntity<>(exceptionError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {NullPointerException.class})
    protected ResponseEntity<ApiErrorDto> handleNullPointerException(RuntimeException ex) {
        log.error("NullPointerException was caught!");

        ApiErrorDto exceptionError = new ApiErrorDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage());

        return new ResponseEntity<>(exceptionError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {EntityNotFoundException.class})
    protected ResponseEntity<ApiErrorDto> handleEntityNotFoundException(RuntimeException ex) {
        log.error("EntityNotFoundException was caught!");

        ApiErrorDto exceptionError = new ApiErrorDto(HttpStatus.NOT_FOUND.value(), ex.getMessage());

        return new ResponseEntity<>(exceptionError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {ValidationException.class})
    protected ResponseEntity<ApiErrorDto> handleValidationException(RuntimeException ex) {
        log.error("ValidationException was caught!");

        ApiErrorDto exceptionError = new ApiErrorDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage());

        return new ResponseEntity<>(exceptionError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {IbanFormatException.class, InvalidCheckDigitException.class, UnsupportedCountryException.class})
    protected ResponseEntity<ApiErrorDto> handleIbanExceptions(RuntimeException ex) {
        log.error("IbanException was caught!");

        ApiErrorDto exceptionError = new ApiErrorDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage());

        return new ResponseEntity<>(exceptionError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {JsonProcessingException.class})
    protected ResponseEntity<ApiErrorDto> handleJsonProcessingException(RuntimeException ex) {
        log.error("JsonProcessingException was caught!");

        ApiErrorDto exceptionError = new ApiErrorDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage());

        return new ResponseEntity<>(exceptionError, HttpStatus.BAD_REQUEST);
    }
}
