package com.ht.elearning.processor;

import com.corundumstudio.socketio.SocketIOServer;
import com.ht.elearning.classroom.Classroom;
import com.ht.elearning.invitation.Invitation;
import com.ht.elearning.mail.MailService;
import com.ht.elearning.notification.NotificationService;
import com.ht.elearning.user.User;
import com.ht.elearning.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class NotificationProcessor {
    private final MailService mailService;
    private final SocketIOServer socketIOServer;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    @Async
    public void processClassroomInvitation(Invitation invitation, Classroom classroom) throws MessagingException {
        var urlString = "https://example.com/classroom/invitation?invite_code=" + classroom.getInviteCode();
        URL acceptUrl = null;
        var user = userRepository.findByEmail(invitation.getEmail()).orElse(null);
        try {
            acceptUrl = new URI(urlString).toURL();

            if (user != null) {
                var notification = notificationService.createClassroomInvitation(user, classroom);

                socketIOServer.getNamespace("/notification")
                        .getAllClients()
                        .stream()
                        .filter(client -> client.getHandshakeData().getHttpHeaders().get("x-auth-id").equals(user.getId()))
                        .findFirst()
                        .ifPresent(client -> {
                            HashMap<String, String> eventData = new HashMap<>();
                            eventData.put("notification_id", notification.getId());
                            client.sendEvent("notification:create", eventData);
                        });

            }

            mailService.sendClassroomInvitationMail(invitation.getEmail(), "ELearning - Classroom invitation", acceptUrl.toString());
        } catch (MalformedURLException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void processClassroomJoining(Classroom classroom, User user) {
        var notification = notificationService.createJoiningClassroomNotification(user, classroom);

        socketIOServer.getNamespace("/notification")
                .getAllClients()
                .stream()
                .filter(client -> client.getHandshakeData().getHttpHeaders().get("x-auth-id").equals(classroom.getOwner().getId()))
                .findFirst()
                .ifPresent(client -> {
                    HashMap<String, String> eventData = new HashMap<>();
                    eventData.put("notification_id", notification.getId());
                    client.sendEvent("notification:create", eventData);
                });
    }

    @Async
    public void processClassroomLeaving(Classroom classroom, User user) {
        var notification = notificationService.createLeavingClassroomNotification(user, classroom);

        socketIOServer.getNamespace("/notification")
                .getAllClients()
                .stream()
                .filter(client -> client.getHandshakeData().getHttpHeaders().get("x-auth-id").equals(classroom.getOwner().getId()))
                .findFirst()
                .ifPresent(client -> {
                    HashMap<String, String> eventData = new HashMap<>();
                    eventData.put("notification_id", notification.getId());
                    client.sendEvent("notification:create", eventData);
                });
    }
}
