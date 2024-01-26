package com.ht.elearning.classwork.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ht.elearning.classwork.ClassworkType;
import com.ht.elearning.constants.ValidationMessage;
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
    @NotEmpty(message = ValidationMessage.TITLE_NOT_EMPTY)
    private String title;

    private String description;

    @NotNull(message = ValidationMessage.CLASSWORK_TYPE_NOT_NULL)
    private ClassworkType type;

    @NotNull(message = ValidationMessage.SCORE_NOT_NULL)
    private int score;

    private Date deadline;

    @NotNull(message = ValidationMessage.ASSIGNEE_IDS_NOT_NULL)
    @JsonProperty("assignee_ids")
    private Set<String> assigneeIds;

    @NotNull(message = ValidationMessage.FILES_ID_NOT_NULL)
    @JsonProperty("file_ids")
    private Set<String> fileIds;

    @JsonProperty("category_id")
    private String categoryId;
}
