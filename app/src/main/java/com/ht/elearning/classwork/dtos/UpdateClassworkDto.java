package com.ht.elearning.classwork.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClassworkDto {
    private String title;
    private String description;
    private int score;
    private Date deadline;

    @JsonProperty("assignee_ids")
    private List<String> assigneeIds;

    @JsonProperty("file_ids")
    private List<String> fileIds;

    @JsonProperty("category_id")
    private String categoryId;
}
