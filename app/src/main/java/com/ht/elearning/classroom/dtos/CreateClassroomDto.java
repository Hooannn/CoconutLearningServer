package com.ht.elearning.classroom.dtos;

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
public class CreateClassroomDto {
    @NotEmpty(message = ValidationMessage.NAME_NOT_EMPTY)
    private String name;

    @JsonProperty("cover_image_url")
    private String coverImageUrl;
    private String description;
    private String room;
    private String course;
}
