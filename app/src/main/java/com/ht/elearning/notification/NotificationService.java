package com.ht.elearning.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository repository;
    public List<Notification> getMyNotifications(String userId) {
        return repository.findAllByRecipientId(userId);
    }
}
