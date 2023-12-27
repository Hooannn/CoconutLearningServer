package com.ht.elearning.invitation;

import com.ht.elearning.classroom.Classroom;
import com.ht.elearning.config.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(InvitationId.class)
@Table(
        name = "invitations"
)
public class Invitation extends BaseEntity {
    @Id
    @Column(nullable = false)
    private String email;

    @Id
    @ManyToOne
    @JoinColumn(
            name = "classroom_id", nullable = false
    )
    private Classroom classroom;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class InvitationId implements Serializable {
    private String email;
    private String classroom;
}