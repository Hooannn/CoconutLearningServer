package com.ht.elearning.invitation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ht.elearning.classroom.Classroom;
import com.ht.elearning.config.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Builder
@Getter
@Setter
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
    @JsonIgnore
    @ManyToOne
    @JoinColumn(
            name = "classroom_id", nullable = false
    )
    private Classroom classroom;

    @Enumerated(value = EnumType.STRING)
    private InvitationType type;
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class InvitationId implements Serializable {
    private String email;
    private String classroom;
}