package com.ht.elearning.assignment.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGradeDto {
    @NotNull(message = "Grade must not be null")
    private double grade;

    private String comment;
}
