package com.ht.elearning.classwork;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ht.elearning.classroom.Classroom;
import com.ht.elearning.config.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "classwork_categories",
        indexes = {
                @Index(name = "idx_classwork_category_classroom", columnList = "classroom_id")
        }
)
public class ClassworkCategory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "classroom_id", nullable = false)
    private Classroom classroom;
}
