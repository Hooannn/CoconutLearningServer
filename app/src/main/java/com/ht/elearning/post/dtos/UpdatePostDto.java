package com.ht.elearning.post.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ht.elearning.constants.ValidationMessage;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostDto {
    private String body;

    @JsonProperty("file_ids")
    private Set<String> fileIds;

    @NotEmpty(message = ValidationMessage.CLASSROOM_ID_NOT_EMPTY)
    @JsonProperty("classroom_id")
    private String classroomId;
}
