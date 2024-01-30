package com.ht.elearning.meeting;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ht.elearning.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(MeetingScheduleId.class)
@Table(name = "meeting_schedules",
        indexes = {
                @Index(name = "idx_meeting_schedules_reminded", columnList = "reminded")
        })
public class MeetingSchedule {
    @Column(name = "scheduled_time", nullable = false)
    private Date scheduledTime;

    @Column(columnDefinition = "boolean default false")
    private boolean reminded;

    @JsonIgnore
    @ManyToOne
    @Id()
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    @JsonIgnore
    @ManyToOne
    @Id()
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

class MeetingScheduleId implements Serializable {
    private String meeting;
    private String user;
}