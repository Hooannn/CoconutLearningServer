package com.ht.elearning.classroom.dtos;

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
public class CreateClassroomDto {
    @NotEmpty(message = "Name must be not empty")
    private String name;

    @JsonProperty("cover_image_url")
    private String coverImageUrl;
    private String description;
    private String room;
    private String course;
}
