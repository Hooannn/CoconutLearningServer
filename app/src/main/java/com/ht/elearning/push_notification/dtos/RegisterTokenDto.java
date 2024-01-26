package com.ht.elearning.push_notification.dtos;

import com.ht.elearning.constants.ValidationMessage;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class RegisterTokenDto {
    @NotNull(message = ValidationMessage.PLATFORM_NOT_NULL)
    private Platform platform;

    @NotEmpty(message = ValidationMessage.TOKEN_NOT_EMPTY)
    private String token;
}


