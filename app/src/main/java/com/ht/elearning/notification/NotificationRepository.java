package com.ht.elearning.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    public List<Notification> findAllByRecipientId(String recipientId);
}
