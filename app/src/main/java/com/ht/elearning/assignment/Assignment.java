package com.ht.elearning.assignment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ht.elearning.classwork.Classwork;
import com.ht.elearning.config.BaseEntity;
import com.ht.elearning.file.File;
import com.ht.elearning.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "assignments",
        indexes = {
                @Index(name = "idx_assignment_classwork", columnList = "classwork_id")
        }
)
public class Assignment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "classwork_id", nullable = false)
    private Classwork classwork;

    @ManyToOne()
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToMany
    @JoinTable(
            name = "assignment_file",
            joinColumns = @JoinColumn(name = "assignment_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "file_id", nullable = false)
    )
    private List<File> files;

    @Column(columnDefinition = "TEXT")
    private String description;

    private int score;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private boolean submitted;
}
