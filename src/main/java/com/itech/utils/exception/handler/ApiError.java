package com.itech.utils.exception.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Collections;
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
    @JsonProperty("Code")
    private String code;
    @JsonProperty("Errors")
    private List<String> errors;

    public ApiError(String code, String error){
        this.code = code;
        this.errors = Arrays.asList(error);
    }
}
