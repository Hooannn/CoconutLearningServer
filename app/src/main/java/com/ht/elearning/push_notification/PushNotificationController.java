package com.ht.elearning.push_notification;

import com.ht.elearning.config.Response;
import com.ht.elearning.constants.ResponseMessage;
import com.ht.elearning.push_notification.dtos.RegisterTokenDto;
import com.ht.elearning.push_notification.dtos.RemoveTokenDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/push_notifications")
@CrossOrigin
public class PushNotificationController {
    private final PushNotificationService pushNotificationService;

    @Operation(summary = "Register a token for push notification")
    @PostMapping("/register")
    public ResponseEntity<Response<FcmToken>> registerToken(@Valid @RequestBody RegisterTokenDto registerTokenDto) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var token = pushNotificationService.registerToken(registerTokenDto, authentication.getPrincipal().toString());
        return ResponseEntity.created(null).body(
                new Response<>(
                        HttpStatus.CREATED.value(),
                        ResponseMessage.TOKEN_CREATED,
                        true,
                        token
                )
        );
    }

    @Operation(summary = "Remove a token for push notification")
    @PostMapping("/remove")
    public ResponseEntity<?> removeToken(@Valid @RequestBody RemoveTokenDto removeTokenDto) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var token = pushNotificationService.removeToken(removeTokenDto, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.TOKEN_REMOVED,
                        true,
                        token
                )
        );
    }
}
