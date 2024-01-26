package com.ht.elearning.assignment.dtos;

import com.ht.elearning.constants.ValidationMessage;
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
    @NotNull(message = ValidationMessage.GRADE_NOT_NULL)
    private double grade;

    private String comment;
}
