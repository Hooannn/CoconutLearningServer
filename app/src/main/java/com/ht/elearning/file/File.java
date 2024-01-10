package com.ht.elearning.file;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("content_type")
    private String contentType;

    private String name;

    @JsonProperty("e_tag")
    private String eTag;
    
    private long size;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;
}
