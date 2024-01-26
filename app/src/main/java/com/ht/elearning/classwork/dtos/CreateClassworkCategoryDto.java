package com.ht.elearning.classwork.dtos;

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
public class CreateClassworkCategoryDto {
    @NotEmpty(message = ValidationMessage.NAME_NOT_EMPTY)
    private String name;
}
