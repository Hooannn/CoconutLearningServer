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
public class CreateClassworkCommentDto {
    @NotEmpty(message = ValidationMessage.BODY_NOT_EMPTY)
    private String body;

    @NotEmpty(message = ValidationMessage.CLASSWORK_ID_NOT_EMPTY)
    @JsonProperty("classwork_id")
    private String classworkId;

    @NotEmpty(message = ValidationMessage.CLASSROOM_ID_NOT_EMPTY)
    @JsonProperty("classroom_id")
    private String classroomId;
}