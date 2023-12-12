package com.ht.elearning.classroom.dtos;

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

    private String description;
    private String room;
    private String course;
}
