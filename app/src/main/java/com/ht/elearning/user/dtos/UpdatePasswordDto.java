package com.ht.elearning.user.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ht.elearning.constants.ValidationMessage;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordDto {
    @NotNull(message = ValidationMessage.OLD_PASSWORD_NOT_NULL)
    @JsonProperty("old_password")
    private String oldPassword;

    @NotNull(message = ValidationMessage.NEW_PASSWORD_NOT_NULL)
    @JsonProperty("new_password")
    private String newPassword;
}
