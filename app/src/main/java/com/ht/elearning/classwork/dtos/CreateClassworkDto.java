package com.ht.elearning.classwork.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ht.elearning.classwork.ClassworkType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateClassworkDto {
    @NotEmpty(message = "Name must not be empty")
    private String title;

    private String description;

    @NotNull(message = "Classwork type must not be null")
    private ClassworkType type;

    private int score;

    private Date deadline;

    @NotNull(message = "Assignee ids must not be null")
    @JsonProperty("assignee_ids")
    private Set<String> assigneeIds;

    @NotNull(message = "File ids must not be null")
    @JsonProperty("file_ids")
    private Set<String> fileIds;

    @JsonProperty("category_id")
    private String categoryId;
}
