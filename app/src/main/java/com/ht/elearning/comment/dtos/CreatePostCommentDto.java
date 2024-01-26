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
public class CreatePostCommentDto {
    @NotEmpty(message = ValidationMessage.BODY_NOT_EMPTY)
    private String body;

    @NotEmpty(message = ValidationMessage.POST_ID_NOT_EMPTY)
    @JsonProperty("post_id")
    private String postId;

    @NotEmpty(message = ValidationMessage.CLASSROOM_ID_NOT_EMPTY)
    @JsonProperty("classroom_id")
    private String classroomId;
}
