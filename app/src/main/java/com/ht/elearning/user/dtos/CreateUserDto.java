package com.ht.elearning.user.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ht.elearning.constants.ValidationMessage;
import com.ht.elearning.user.Role;
import com.ht.elearning.user.validation.CreatableRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDto {
    @NotEmpty(message = ValidationMessage.PASSWORD_NOT_EMPTY)
    @Min(value = 6, message = ValidationMessage.PASSWORD_MIN_6_CHARS)
    private String password;

    @NotEmpty(message = ValidationMessage.EMAIL_NOT_EMPTY)
    @Email(message = ValidationMessage.EMAIL_VALID)
    private String email;

    @NotEmpty(message = ValidationMessage.FIRST_NAME_NOT_EMPTY)
    private String firstName;

    @NotEmpty(message = ValidationMessage.LAST_NAME_NOT_EMPTY)
    private String lastName;

    @NotNull(message = ValidationMessage.ROLE_NOT_NULL)
    @CreatableRole(message = ValidationMessage.INVALID_ROLE)
    private Role role;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    private boolean verified;
}
