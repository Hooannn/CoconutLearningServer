package com.ht.elearning.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ht.elearning.classroom.Classroom;
import com.ht.elearning.comment.Comment;
import com.ht.elearning.config.BaseEntity;
import com.ht.elearning.file.File;
import com.ht.elearning.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "posts",
        indexes = {
                @Index(name = "idx_post_classroom", columnList = "classroom_id")
        }
)
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @JsonIgnore
    @ManyToOne()
    @JoinColumn(name = "classroom_id", nullable = false)
    private Classroom classroom;

    @ManyToOne()
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @OneToMany(mappedBy = "post", targetEntity = Comment.class, cascade = CascadeType.REMOVE)
    private List<Comment> comments;

    @ManyToMany
    @JoinTable(
            name = "post_file",
            joinColumns = @JoinColumn(name = "post_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "file_id", nullable = false)
    )
    private Set<File> files;
}
