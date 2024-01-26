package com.ht.elearning.push_notification.dtos;

import com.ht.elearning.constants.ValidationMessage;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RemoveTokenDto {
    @NotNull(message = ValidationMessage.PLATFORM_NOT_NULL)
    private Platform platform;
}
