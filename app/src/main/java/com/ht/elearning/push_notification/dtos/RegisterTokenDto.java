package com.ht.elearning.push_notification.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class RegisterTokenDto {
    @NotNull(message = "Platform must not be null")
    private Platform platform;

    @NotEmpty(message = "Token must not be empty")
    private String token;
}


