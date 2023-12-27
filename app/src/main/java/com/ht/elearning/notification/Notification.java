package com.ht.elearning.notification;

import com.ht.elearning.config.BaseEntity;
import com.ht.elearning.user.User;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "idx_recipient", columnList = "recipient_id")
        }
)
public class Notification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String title;
    private String content;
    private boolean read;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;
}
