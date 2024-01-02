package com.ht.elearning.classroom;

import com.ht.elearning.config.BaseEntity;
import com.ht.elearning.invitation.Invitation;
import com.ht.elearning.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Reference;

import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "classrooms",
        indexes = {
                @Index(name = "idx_owner", columnList = "owner_id")
        }
)
public class Classroom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String inviteCode;

    @Column(nullable = false)
    private String name;

    private String description;
    private String room;
    private String course;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToMany
    @JoinTable(
            name = "classroom_provider",
            joinColumns = @JoinColumn(name = "classroom_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "provider_id", nullable = false)
    )
    private List<User> providers;

    @ManyToMany
    @JoinTable(
            name = "classroom_user",
            joinColumns = @JoinColumn(name = "classroom_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "user_id", nullable = false)
    )
    private List<User> users;

    @OneToMany(mappedBy = "classroom", targetEntity = Invitation.class)
    private List<Invitation> invitations;
}
