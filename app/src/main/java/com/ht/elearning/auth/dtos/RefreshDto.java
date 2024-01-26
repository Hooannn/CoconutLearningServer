package com.ht.elearning.auth.dtos;

import com.ht.elearning.constants.ValidationMessage;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshDto {
    @NotEmpty(message = ValidationMessage.TOKEN_NOT_EMPTY)
    private String token;
}
