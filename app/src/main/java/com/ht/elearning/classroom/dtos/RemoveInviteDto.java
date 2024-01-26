package com.ht.elearning.classroom.dtos;

import com.ht.elearning.constants.ValidationMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RemoveInviteDto {
    @NotEmpty(message = ValidationMessage.EMAIL_NOT_EMPTY)
    @Email(message = ValidationMessage.EMAIL_VALID)
    private String email;
}
