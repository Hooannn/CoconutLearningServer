package com.ht.elearning.auth.dtos;

import com.ht.elearning.push_notification.dtos.Platform;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignOutDto {
    @NotNull(message = "Platform must not be null")
    private Platform platform;
}