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
public class CreateClassworkCommentDto {
    @NotEmpty(message = "Body must not be empty")
    private String body;

    @NotEmpty(message = "Classwork id must not be empty")
    @JsonProperty("classwork_id")
    private String classworkId;

    @NotEmpty(message = "Classroom id must not be empty")
    @JsonProperty("classroom_id")
    private String classroomId;
}