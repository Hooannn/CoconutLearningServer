package com.ht.elearning.notification;

import com.ht.elearning.config.QueryResponse;
import com.ht.elearning.config.Response;
import com.ht.elearning.constants.ResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/notifications")
@CrossOrigin
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "Find all notifications of the current user")
    @GetMapping("/own")
    public ResponseEntity<QueryResponse<List<Notification>>> findMyNotifications(@RequestParam(defaultValue = "0") int skip,
                                                                                 @RequestParam(defaultValue = "20") int limit) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var queryPair = notificationService.findMyNotifications(authentication.getPrincipal().toString(), skip, limit);
        return ResponseEntity.ok(
                new QueryResponse<>(
                        queryPair.total(),
                        queryPair.results().size(),
                        HttpStatus.OK.value(),
                        "Ok",
                        true,
                        queryPair.results()
                )
        );
    }

    @Operation(summary = "Delete all notifications of the current user")
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

    @Operation(summary = "Count unread notifications of the current user")
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

    @Operation(summary = "Mark all notifications of the current user as read")
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

    @Operation(summary = "Mark a notification by its id as read")
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
