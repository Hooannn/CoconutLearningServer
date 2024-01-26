package com.ht.elearning.assignment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ht.elearning.classwork.Classwork;
import com.ht.elearning.config.BaseEntity;
import com.ht.elearning.file.File;
import com.ht.elearning.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(AssignmentId.class)
@Entity
@Table(
        name = "assignments",
        indexes = {
                @Index(name = "idx_assignment_classwork", columnList = "classwork_id"),
                @Index(name = "idx_assignment_user", columnList = "author_id")
        }
)
public class Assignment extends BaseEntity {
    @JsonIgnore
    @Id
    @ManyToOne
    @JoinColumn(name = "classwork_id", nullable = false)
    private Classwork classwork;

    @Id
    @ManyToOne()
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToMany
    @JoinTable(
            name = "assignment_file",
            joinColumns = {
                    @JoinColumn(name = "classwork_id", nullable = false),
                    @JoinColumn(name = "author_id", nullable = false)
            },
            inverseJoinColumns = @JoinColumn(name = "file_id", nullable = false)
    )
    private Set<File> files;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private boolean submitted;

    @OneToOne
    @JoinColumn(name = "grade_id")
    private Grade grade;
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class AssignmentId implements Serializable {
    private String classwork;
    private String author;
}