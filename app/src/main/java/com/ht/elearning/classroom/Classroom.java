package com.ht.elearning.classroom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ht.elearning.classwork.ClassworkCategory;
import com.ht.elearning.config.BaseEntity;
import com.ht.elearning.invitation.Invitation;
import com.ht.elearning.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EqualsAndHashCode(callSuper = true)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "classrooms",
        indexes = {
                @Index(name = "idx_classroom_owner", columnList = "owner_id")
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "classroom_provider",
            joinColumns = @JoinColumn(name = "classroom_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "provider_id", nullable = false)
    )
    private Set<User> providers;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "classroom_user",
            joinColumns = @JoinColumn(name = "classroom_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "user_id", nullable = false)
    )
    private Set<User> users;

    @OneToMany(mappedBy = "classroom", targetEntity = Invitation.class, cascade = CascadeType.REMOVE)
    private List<Invitation> invitations;

    @JsonProperty("classwork_categories")
    @OneToMany(mappedBy = "classroom", targetEntity = ClassworkCategory.class, cascade = CascadeType.REMOVE)
    private List<ClassworkCategory> classworkCategories;

    @JsonIgnore
    public Set<User> getMembers() {
        Stream<User> usersAndProvidersStream = Stream.concat(getProviders().stream(), getUsers().stream());
        return Stream.concat(usersAndProvidersStream, Stream.of(owner)).collect(Collectors.toSet());
    }
}
