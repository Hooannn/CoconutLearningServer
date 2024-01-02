package com.ht.elearning.classwork.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateClassworkCategoryDto {
    @NotEmpty(message = "Name must not be empty")
    private String name;
}
