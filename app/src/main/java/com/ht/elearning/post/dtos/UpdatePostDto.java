package com.ht.elearning.post.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostDto {
    private String body;

    @JsonProperty("file_ids")
    private List<String> fileIds;

    @NotEmpty(message = "Classroom id must not be empty")
    @JsonProperty("classroom_id")
    private String classroomId;
}
