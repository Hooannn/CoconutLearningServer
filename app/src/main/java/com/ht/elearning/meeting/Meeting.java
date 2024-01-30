package com.ht.elearning.meeting;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ht.elearning.classroom.Classroom;
import com.ht.elearning.config.BaseEntity;
import com.ht.elearning.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "meetings",
        indexes = {
                @Index(name = "idx_meeting_classroom", columnList = "classroom_id")
        }
)
public class Meeting extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "classroom_id", nullable = false)
    private Classroom classroom;

    @ManyToOne
    @JoinColumn(name = "created_by_id", nullable = false)
    @JsonProperty("created_by")
    private User createdBy;

    @Column(nullable = false, unique = true)
    @JsonProperty("start_at")
    private Date startAt;

    @Column(nullable = false, unique = true, columnDefinition = "timestamp(6) check (start_at < end_at)")
    @JsonProperty("end_at")
    private Date endAt;

    @JsonIgnore
    @OneToMany(mappedBy = "meeting", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, targetEntity = MeetingSchedule.class)
    private List<MeetingSchedule> schedules;
}
