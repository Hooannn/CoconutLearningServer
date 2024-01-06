package com.ht.elearning.classroom.dtos;

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
    @NotEmpty(message = "Email must not be empty")
    @Email(message = "Email must be valid")
    private String email;
}
