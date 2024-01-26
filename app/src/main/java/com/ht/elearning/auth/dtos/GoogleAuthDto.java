package com.ht.elearning.auth.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ht.elearning.constants.ValidationMessage;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleAuthDto {
    @JsonProperty("access_token")
    @NotEmpty(message = ValidationMessage.ACCESS_TOKEN_NOT_EMPTY)
    private String accessToken;
}
