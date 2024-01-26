package com.ht.elearning.classroom.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ht.elearning.constants.ValidationMessage;
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
    @NotEmpty(message = ValidationMessage.EMAILS_NOT_EMPTY)
    private Set<@Email(message = ValidationMessage.EMAIL_VALID) String> emails;

    @JsonProperty("class_id")
    @NotEmpty(message = ValidationMessage.CLASS_ID_NOT_EMPTY)
    private String classId;

    @NotNull(message = ValidationMessage.TYPE_NOT_NULL)
    private InvitationType type;
}
