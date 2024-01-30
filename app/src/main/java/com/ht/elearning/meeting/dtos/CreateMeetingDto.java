package com.ht.elearning.meeting.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ht.elearning.constants.ValidationMessage;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMeetingDto {
    @NotEmpty(message = ValidationMessage.NAME_NOT_EMPTY)
    private String name;

    @NotNull(message = ValidationMessage.START_TIME_NOT_NULL)
    @JsonProperty("start_at")
    private Date startAt;

    @NotNull(message = ValidationMessage.END_TIME_NOT_NULL)
    @JsonProperty("end_at")
    private Date endAt;
}
