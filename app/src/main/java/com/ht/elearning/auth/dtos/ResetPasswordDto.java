package com.ht.elearning.auth.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ht.elearning.constants.ValidationMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordDto {
    @NotEmpty(message = ValidationMessage.EMAIL_NOT_EMPTY)
    @Email(message = ValidationMessage.EMAIL_VALID)
    private String email;

    @NotEmpty(message = ValidationMessage.NEW_PASSWORD_NOT_EMPTY)
    @Length(min = 6, message = ValidationMessage.NEW_PASSWORD_MIN_6_CHARS)
    @JsonProperty("new_password")
    private String newPassword;

    @NotEmpty(message = ValidationMessage.SIGNATURE_NOT_EMPTY)
    private String signature;
}
