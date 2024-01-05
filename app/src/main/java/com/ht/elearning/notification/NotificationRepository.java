package com.ht.elearning.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findAllByRecipientId(String recipientId);

    Long countByRecipientIdAndReadFalse(String recipientId);

    @Transactional
    void deleteAllByRecipientId(String userId);

    Optional<Notification> findByIdAndRecipientId(String notificationId, String userId);

    List<Notification> findAllByRecipientIdAndReadFalse(String userId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE notifications SET read = true WHERE recipient_id = ?1 AND read = false", nativeQuery = true)
    void markNotificationsAsRead(String recipientId);
}
