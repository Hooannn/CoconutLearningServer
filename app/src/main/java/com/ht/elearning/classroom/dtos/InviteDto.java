package com.ht.elearning.classroom.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ht.elearning.invitation.InvitationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InviteDto {
    @NotEmpty(message = "Email must not be empty")
    @Email(message = "Email must be valid")
    private String email;

    @JsonProperty("class_id")
    @NotEmpty(message = "Class id must not be empty")
    private String classId;

    @NotNull(message = "Type must not be null")
    private InvitationType type;
}
