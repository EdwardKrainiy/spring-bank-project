package com.itech.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CreationRequestDto data-transfer object to manipulate with DB.
 *
 * @author Edvard Krainiy on 01/05/2022
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreationRequestDto {
    @JsonProperty("Id")
    private Long id;

    @JsonProperty("UserId")
    private Long userId;

    @JsonProperty("Payload")
    private String payload;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("CreatedId")
    private Long createdId;

    @JsonProperty("IssuedAt")
    private String issuedAt;

    @JsonProperty("CreationType")
    private String creationType;
}
