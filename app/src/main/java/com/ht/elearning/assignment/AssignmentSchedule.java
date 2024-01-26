package com.ht.elearning.assignment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ht.elearning.classwork.Classwork;
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
@IdClass(AssignmentScheduleId.class)
@Table(name = "assignment_schedules",
        indexes = {
                @Index(name = "idx_assignment_schedules_reminded", columnList = "reminded")
        })
public class AssignmentSchedule {
    @Column(name = "scheduled_time", nullable = false)
    private Date scheduledTime;

    @Column(columnDefinition = "boolean default false")
    private boolean reminded;

    @JsonIgnore
    @ManyToOne
    @Id()
    @JoinColumn(name = "classwork_id", nullable = false)
    private Classwork classwork;

    @JsonIgnore
    @ManyToOne
    @Id()
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

class AssignmentScheduleId implements Serializable {
    private String classwork;
    private String user;
}