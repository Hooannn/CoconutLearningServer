package com.ht.elearning.meeting.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMeetingDto {
    private String name;

    @JsonProperty("start_at")
    private Date startAt;
    
    @JsonProperty("end_at")
    private Date endAt;
}
