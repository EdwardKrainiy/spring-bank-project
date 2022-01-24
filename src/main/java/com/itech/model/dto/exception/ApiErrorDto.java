package com.itech.model.dto.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itech.utils.literal.JsonPropertyText;
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
public class ApiErrorDto {
    @JsonProperty(JsonPropertyText.CODE)
    private int code;
    @JsonProperty(JsonPropertyText.ERRORS)
    private List<String> errors;

    public ApiErrorDto(int code, String error) {
        this.code = code;
        this.errors = Collections.singletonList(error);
    }
}
