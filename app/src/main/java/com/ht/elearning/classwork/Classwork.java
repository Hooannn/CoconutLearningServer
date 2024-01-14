package com.ht.elearning.classwork;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ht.elearning.assignment.Assignment;
import com.ht.elearning.classroom.Classroom;
import com.ht.elearning.comment.Comment;
import com.ht.elearning.config.BaseEntity;
import com.ht.elearning.file.File;
import com.ht.elearning.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "classwork",
        indexes = {
                @Index(name = "idx_classwork_classroom", columnList = "classroom_id"),
                @Index(name = "idx_classwork_category", columnList = "category_id")
        }
)
public class Classwork extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "title", nullable = false)
    private String title;

    private String description;

    @Enumerated(value = EnumType.STRING)
    private ClassworkType type;

    private int score;

    private Date deadline;

    @OneToMany(mappedBy = "classwork", targetEntity = Comment.class, cascade = CascadeType.REMOVE)
    private List<Comment> comments;

    @OneToMany(mappedBy = "classwork", targetEntity = Assignment.class, cascade = CascadeType.REMOVE)
    private List<Assignment> assignments;

    @ManyToMany
    @JoinTable(
            name = "classwork_user",
            joinColumns = @JoinColumn(name = "classwork_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "user_id", nullable = false)
    )
    private List<User> assignees;

    @ManyToMany
    @JoinTable(
            name = "classwork_file",
            joinColumns = @JoinColumn(name = "classwork_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "file_id", nullable = false)
    )
    private List<File> files;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "classroom_id", nullable = false)
    private Classroom classroom;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private ClassworkCategory category;

    @ManyToOne()
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
}
