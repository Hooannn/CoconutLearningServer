package com.ht.elearning.notification;

import com.ht.elearning.config.QueryResponse;
import com.ht.elearning.config.Response;
import com.ht.elearning.constants.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/notifications")
@CrossOrigin
public class NotificationController {
    private final NotificationService notificationService;


    @GetMapping("/own")
    public ResponseEntity<QueryResponse<List<Notification>>> findMyNotifications() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var notifications = notificationService.findMyNotifications(authentication.getPrincipal().toString());
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


    @DeleteMapping("/own")
    public ResponseEntity<Response<?>> deleteMyNotifications() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = notificationService.deleteMyNotifications(authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.OK,
                        success,
                        null
                )
        );
    }


    @GetMapping("/unread/count")
    public ResponseEntity<Response<Long>> countUnread() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var count = notificationService.countUnread(authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.OK,
                        true,
                        count
                )
        );
    }


    @PostMapping("/mark-all")
    public ResponseEntity<Response<?>> markAll() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = notificationService.markAll(authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.OK,
                        success,
                        null
                )
        );
    }


    @PostMapping("/mark/{notificationId}")
    public ResponseEntity<Response<?>> mark(@PathVariable String notificationId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = notificationService.mark(notificationId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.OK,
                        success,
                        null
                )
        );
    }
}
