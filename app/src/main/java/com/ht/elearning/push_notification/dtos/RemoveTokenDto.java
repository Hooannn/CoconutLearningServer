package com.ht.elearning.push_notification.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RemoveTokenDto {
    @NotNull(message = "Platform must not be null")
    private Platform platform;
}
