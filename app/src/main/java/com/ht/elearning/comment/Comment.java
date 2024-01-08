package com.ht.elearning.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ht.elearning.config.BaseEntity;
import com.ht.elearning.post.Post;
import com.ht.elearning.user.User;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "comments"
)
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @ManyToOne()
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne()
    @JsonIgnore
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}
