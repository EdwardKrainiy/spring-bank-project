package com.itech.model.dto.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itech.utils.literal.DtoJsonProperty;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Custom API Errors class.
 *
 * @author Edvard Krainiy on 12/13/2021
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorDto {
  @JsonProperty(DtoJsonProperty.CODE)
  private int code;

  @JsonProperty(DtoJsonProperty.ERRORS)
  private Set<String> errors;

  public ApiErrorDto(int code, String error) {
    this.code = code;
    this.errors = Set.of(error);
  }
}
