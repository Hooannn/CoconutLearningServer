package com.ht.elearning.post.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ht.elearning.constants.ValidationMessage;
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
public class CreatePostDto {
    @NotEmpty(message = ValidationMessage.BODY_NOT_EMPTY)
    private String body;

    @NotEmpty(message = ValidationMessage.CLASSROOM_ID_NOT_EMPTY)
    @JsonProperty("classroom_id")
    private String classroomId;

    @NotNull(message = ValidationMessage.FILES_ID_NOT_NULL)
    @JsonProperty("file_ids")
    private Set<String> fileIds;
}
