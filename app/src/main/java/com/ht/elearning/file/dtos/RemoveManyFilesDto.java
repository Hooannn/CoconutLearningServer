package com.ht.elearning.file.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ht.elearning.constants.ValidationMessage;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RemoveManyFilesDto {
    @NotEmpty(message = ValidationMessage.FILES_ID_NOT_EMPTY)
    @JsonProperty("file_ids")
    private Set<String> fileIds;
}
