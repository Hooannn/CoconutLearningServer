package com.ht.elearning.assignment.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class CreateAssignmentDto {
    @NotEmpty(message = "Classwork id must not be empty")
    @JsonProperty("classwork_id")
    private String classworkId;

    @NotEmpty(message = "Classroom id must not be empty")
    @JsonProperty("classroom_id")
    private String classroomId;

    @NotNull(message = "File ids must not be null")
    @JsonProperty("file_ids")
    private Set<String> fileIds;

    private String description;

    private boolean submitted;
}
