package com.ht.elearning.auth.dtos;

import com.ht.elearning.constants.ValidationMessage;
import com.ht.elearning.push_notification.dtos.Platform;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignOutDto {
    @NotNull(message = ValidationMessage.PLATFORM_NOT_NULL)
    private Platform platform;
}