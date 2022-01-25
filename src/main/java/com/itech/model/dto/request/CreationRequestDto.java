package com.itech.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itech.utils.literal.JsonPropertyText;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "CreationRequest data-transfer object to manipulate with DB.")
public class CreationRequestDto {
  @JsonProperty(JsonPropertyText.ID)
  @Schema(description = "Unique Id field of CreationRequest.")
  private Long id;

  @JsonProperty(JsonPropertyText.USER_ID)
  @Schema(description = "UserId field of CreationRequest.")
  private Long userId;

  @JsonProperty(JsonPropertyText.PAYLOAD)
  @Schema(
      description =
          "Payload field of CreationRequest. Contains JSON to create Transaction or Account.")
  private String payload;

  @JsonProperty(JsonPropertyText.STATUS)
  @Schema(
      description =
          "Status field of CreationRequest. Can be IN_PROGRESS, CREATED, REJECTED, EXPIRED.")
  private String status;

  @JsonProperty(JsonPropertyText.CREATED_ID)
  @Schema(description = "Unique CreatedId of CreationRequest.")
  private Long createdId;

  @JsonProperty(JsonPropertyText.ISSUED_AT)
  @Schema(description = "IssuedAt field of CreationRequest. Contains time and date of creation.")
  private String issuedAt;

  @JsonProperty(JsonPropertyText.CREATION_TYPE)
  @Schema(description = "CreationType field of CreationRequest. Can be ACCOUNT, TRANSACTION.")
  private String creationType;
}
