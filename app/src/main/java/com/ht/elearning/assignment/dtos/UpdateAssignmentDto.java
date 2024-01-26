package com.ht.elearning.assignment.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ht.elearning.constants.ValidationMessage;
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
public class UpdateAssignmentDto {
    @NotNull(message = ValidationMessage.FILES_ID_NOT_NULL)
    @JsonProperty("file_ids")
    private Set<String> fileIds;

    private String description;
}
