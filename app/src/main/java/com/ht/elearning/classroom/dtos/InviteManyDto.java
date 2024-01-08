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

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InviteManyDto {
    @NotEmpty(message = "Emails must not be empty")
    private Set<@Email(message = "Email must be valid") String> emails;

    @JsonProperty("class_id")
    @NotEmpty(message = "Class id must not be empty")
    private String classId;

    @NotNull(message = "Type must not be null")
    private InvitationType type;
}
