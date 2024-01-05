package com.ht.elearning.push_notification.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RemoveTokenDto {
    @NotNull(message = "Platform must not be null")
    private Platform platform;
}
