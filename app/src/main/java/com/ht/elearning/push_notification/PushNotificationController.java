package com.ht.elearning.push_notification;

import com.ht.elearning.config.Response;
import com.ht.elearning.push_notification.dtos.RegisterTokenDto;
import com.ht.elearning.push_notification.dtos.RemoveTokenDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/push_notifications")
@CrossOrigin
public class PushNotificationController {
    private final PushNotificationService pushNotificationService;

    @PostMapping("/register")
    public ResponseEntity<Response<FcmToken>> registerToken(@Valid @RequestBody RegisterTokenDto registerTokenDto) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var token = pushNotificationService.registerToken(registerTokenDto, authentication.getPrincipal().toString());
        return ResponseEntity.created(null).body(
                new Response<>(
                        HttpStatus.CREATED.value(),
                        "Created token successfully",
                        true,
                        token
                )
        );
    }

    @PostMapping("/remove")
    public ResponseEntity<?> removeToken(@Valid @RequestBody RemoveTokenDto removeTokenDto) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var token = pushNotificationService.removeToken(removeTokenDto, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Removed token",
                        true,
                        token
                )
        );
    }
}
