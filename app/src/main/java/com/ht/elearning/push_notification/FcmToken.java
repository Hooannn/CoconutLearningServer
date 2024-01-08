package com.ht.elearning.push_notification;

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
        name = "fcm_tokens"
)
public class FcmToken extends BaseEntity {
    @Id
    private String uid;

    private String web;
    private String ios;
    private String android;

    @OneToOne
    @JoinColumn(name = "uid", nullable = false)
    private User owner;
}