package com.ht.elearning.meeting.dtos;

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
    private Date startAt;
    private Date endAt;
}
