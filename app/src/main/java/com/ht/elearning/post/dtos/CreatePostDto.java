package com.ht.elearning.post.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostDto {
    @NotEmpty(message = "Body must not be empty")
    private String body;

    @NotEmpty(message = "Classroom id must not be empty")
    @JsonProperty("classroom_id")
    private String classroomId;

    @NotNull(message = "File ids must not be null")
    @JsonProperty("file_ids")
    private List<String> fileIds;
}
