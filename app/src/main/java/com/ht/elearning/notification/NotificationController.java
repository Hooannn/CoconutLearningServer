package com.ht.elearning.notification;

import com.ht.elearning.config.QueryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/notifications")
@CrossOrigin
public class NotificationController {
    private final NotificationService service;

    @GetMapping("/own")
    public ResponseEntity<QueryResponse<List<Notification>>> getMyNotifications() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var notifications = service.getMyNotifications(authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new QueryResponse<>(
                        0,
                        0,
                        HttpStatus.OK.value(),
                        "Ok",
                        true,
                        notifications
                )
        );
    }
}
