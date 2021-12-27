package com.itech.utils.exception.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * Custom API Errors class.
 *
 * @author Edvard Krainiy on 12/13/2021
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {
    @JsonProperty("Message")
    private String message;
    @JsonProperty("Errors")
    private List<String> errors;

    public ApiError(String error) {
        errors = Arrays.asList(error);
    }

    public ApiError(String message, String error) {
        super();
        this.message = message;
        errors = Arrays.asList(error);
    }
}
