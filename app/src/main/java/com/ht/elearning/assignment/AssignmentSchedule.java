package com.ht.elearning.assignment;

import com.ht.elearning.classwork.Classwork;
import com.ht.elearning.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "assignment_schedules",
        indexes = {
                @Index(name = "idx_assignment_schedules_reminded", columnList = "reminded")
        })
public class AssignmentSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheduled_time", nullable = false)
    private Date scheduledTime;

    @Column(columnDefinition = "boolean default false")
    private boolean reminded;

    @ManyToOne()
    @JoinColumn(name = "classwork_id", nullable = false)
    private Classwork classwork;

    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
