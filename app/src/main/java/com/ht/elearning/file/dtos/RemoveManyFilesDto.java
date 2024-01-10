package com.ht.elearning.file.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RemoveManyFilesDto {
    @NotEmpty(message = "File ids must not be empty")
    @JsonProperty("file_ids")
    private List<String> fileIds;
}
