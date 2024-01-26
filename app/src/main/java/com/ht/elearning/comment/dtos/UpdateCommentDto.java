package com.ht.elearning.comment.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ht.elearning.constants.ValidationMessage;
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

    @NotEmpty(message = ValidationMessage.CLASSROOM_ID_NOT_EMPTY)
    @JsonProperty("classroom_id")
    private String classroomId;
}
