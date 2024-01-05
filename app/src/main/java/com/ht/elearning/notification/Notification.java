package com.ht.elearning.notification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ht.elearning.config.BaseEntity;
import com.ht.elearning.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "idx_notification_recipient", columnList = "recipient_id")
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

    @Column(name = "redirect_url")
    @JsonProperty("redirect_url")
    private String redirectUrl;

    @Column(name = "image_url")
    @JsonProperty("image_url")
    private String imageUrl;

    @Column(name = "actions", columnDefinition = "jsonb")
    @Convert(converter = ActionListConverter.class)
    private List<Action> actions;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;
}
