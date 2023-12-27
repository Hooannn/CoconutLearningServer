package com.ht.elearning.classroom.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinDto {
    @JsonProperty("invite_code")
    @NotEmpty(message = "Invite code must not be empty")
    private String inviteCode;
}
