package com.ht.elearning.file;

import com.ht.elearning.config.BaseEntity;
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
        name = "files",
        indexes = {
                @Index(name = "idx_file_creator", columnList = "creator_id")
        }
)
public class File extends BaseEntity {
    @Id
    private String id;

    private String name;
    private String eTag;
    private long size;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;
}
