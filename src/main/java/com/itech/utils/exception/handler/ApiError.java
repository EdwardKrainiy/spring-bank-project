package com.itech.utils.exception.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private int code;
    @JsonProperty("Errors")
    private List<String> errors;

    public ApiError(int code, String error){
        this.code = code;
        this.errors = Collections.singletonList(error);
    }
}
