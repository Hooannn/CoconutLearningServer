package com.ht.elearning.user.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ht.elearning.constants.ValidationMessage;
import com.ht.elearning.user.Role;
import com.ht.elearning.user.validation.CreatableRole;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {
    @Min(value = 6, message = ValidationMessage.PASSWORD_MIN_6_CHARS)
    private String password;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @CreatableRole(message = ValidationMessage.INVALID_ROLE)
    private Role role;

    @JsonProperty("avatar_url")
    private String avatarUrl;
}
