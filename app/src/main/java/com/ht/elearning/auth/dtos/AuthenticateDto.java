package com.ht.elearning.auth.dtos;

import com.ht.elearning.constants.ValidationMessage;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticateDto {
    @NotEmpty(message = ValidationMessage.PASSWORD_NOT_EMPTY)
    @Length(min = 6, message = ValidationMessage.PASSWORD_MIN_6_CHARS)
    private String password;

    @NotEmpty(message = ValidationMessage.EMAIL_NOT_EMPTY)
    @Email(message = ValidationMessage.EMAIL_VALID)
    private String email;
}
