package com.ht.elearning.comment.dtos;

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
public class UpdateCommentDto {
    private String body;

    @NotEmpty(message = "Classroom id must not be empty")
    @JsonProperty("classroom_id")
    private String classroomId;
}
