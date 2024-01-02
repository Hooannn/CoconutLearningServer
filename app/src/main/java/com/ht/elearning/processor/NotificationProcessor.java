package com.ht.elearning.processor;

import com.corundumstudio.socketio.SocketIOServer;
import com.google.firebase.messaging.Notification;
import com.ht.elearning.classroom.Classroom;
import com.ht.elearning.classwork.Classwork;
import com.ht.elearning.comment.Comment;
import com.ht.elearning.invitation.Invitation;
import com.ht.elearning.mail.MailService;
import com.ht.elearning.notification.NotificationService;
import com.ht.elearning.post.Post;
import com.ht.elearning.push_notification.PushNotificationService;
import com.ht.elearning.user.User;
import com.ht.elearning.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class NotificationProcessor {
    private static final Logger logger = LoggerFactory.getLogger(NotificationProcessor.class);
    private final MailService mailService;
    private final SocketIOServer socketIOServer;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final PushNotificationService pushNotificationService;

    @Async
    public void processClassroomInvitation(Invitation invitation, Classroom classroom) {
        var urlString = "https://example.com/classroom/invitation?invite_code=" + classroom.getInviteCode();
        URL acceptUrl = null;
        var user = userRepository.findByEmail(invitation.getEmail()).orElse(null);
        try {
            acceptUrl = new URI(urlString).toURL();

            CompletableFuture<Void> createNotificationFuture = CompletableFuture.runAsync(() -> {
                if (user != null) {
                    var notification = notificationService.createClassroomInvitation(user, classroom);
                    var target = user.getId();
                    socketIOServer.getNamespace("/notification")
                            .getAllClients()
                            .stream()
                            .filter(client -> client.getHandshakeData().getHttpHeaders().get("x-auth-id").equals(target))
                            .findFirst()
                            .ifPresent(client -> {
                                HashMap<String, String> eventData = new HashMap<>();
                                eventData.put("notification_id", notification.getId());
                                client.sendEvent("notification:create", eventData);
                            });

                    try {
                        var batchResponse = pushNotificationService.push(
                                target,
                                Notification.builder()
                                        .setBody(notification.getContent())
                                        .setTitle(notification.getTitle())
                                        .setImage(notification.getImageUrl())
                                        .build(),
                                null
                        );
                        logger.debug(
                                "Handle[processClassroomInvitation] - Push batch response - SuccessCount[{}] - FailureCount[{}] - Responses[{}]",
                                batchResponse.getSuccessCount(), batchResponse.getFailureCount(), batchResponse.getResponses()
                        );
                    } catch (Exception e) {
                        logger.warn(
                                "Handle[processClassroomInvitation] - Catch exception while pushing notification - UserId[{}] - Title[{}] - Body[{}] - ImageUrl[{}] - Message[{}]",
                                target, notification.getTitle(), notification.getContent(), notification.getImageUrl(), e.getMessage()
                        );
                    }
                }
            });

            URL finalAcceptUrl = acceptUrl;
            CompletableFuture<Void> sendMailFuture = CompletableFuture.runAsync(() -> {
                String mailSubject = "ELearning - Classroom invitation";
                try {
                    mailService.sendClassroomInvitationMail(invitation.getEmail(), mailSubject, finalAcceptUrl.toString());
                } catch (MessagingException e) {
                    logger.error(
                            "Error while sending email - Email[{}] - Subject[{}] - Message[{}]",
                            invitation.getEmail(),
                            mailSubject,
                            e.getMessage()
                    );
                }
            });

            CompletableFuture.allOf(createNotificationFuture, sendMailFuture).join();
        } catch (MalformedURLException | URISyntaxException e) {
            logger.error(
                    "Error while doing processClassroomInvitation - Email[{}] - ClassroomId[{}] - Message[{}]",
                    invitation.getEmail(),
                    classroom.getId(),
                    e.getMessage()
            );
        }
    }

    @Async
    public void processClassroomJoining(Classroom classroom, User user) {
        var notification = notificationService.createJoiningClassroomNotification(user, classroom);
        var target = classroom.getOwner().getId();
        socketIOServer.getNamespace("/notification")
                .getAllClients()
                .stream()
                .filter(client -> client.getHandshakeData().getHttpHeaders().get("x-auth-id").equals(target))
                .findFirst()
                .ifPresent(client -> {
                    HashMap<String, String> eventData = new HashMap<>();
                    eventData.put("notification_id", notification.getId());
                    client.sendEvent("notification:create", eventData);
                });

        try {
            var batchResponse = pushNotificationService.push(
                    target,
                    Notification.builder()
                            .setBody(notification.getContent())
                            .setTitle(notification.getTitle())
                            .setImage(notification.getImageUrl())
                            .build(),
                    null
            );
            logger.debug(
                    "Handle[processClassroomJoining] - Push batch response - SuccessCount[{}] - FailureCount[{}] - Responses[{}]",
                    batchResponse.getSuccessCount(), batchResponse.getFailureCount(), batchResponse.getResponses()
            );
        } catch (Exception e) {
            logger.warn(
                    "Handle[processClassroomJoining] - Catch exception while pushing notification - UserId[{}] - Title[{}] - Body[{}] - ImageUrl[{}] - Message[{}]",
                    target, notification.getTitle(), notification.getContent(), notification.getImageUrl(), e.getMessage()
            );
        }
    }

    @Async
    public void processClassroomLeaving(Classroom classroom, User user) {
        var notification = notificationService.createLeavingClassroomNotification(user, classroom);
        var target = classroom.getOwner().getId();
        socketIOServer.getNamespace("/notification")
                .getAllClients()
                .stream()
                .filter(client -> client.getHandshakeData().getHttpHeaders().get("x-auth-id").equals(target))
                .findFirst()
                .ifPresent(client -> {
                    HashMap<String, String> eventData = new HashMap<>();
                    eventData.put("notification_id", notification.getId());
                    client.sendEvent("notification:create", eventData);
                });

        try {
            var batchResponse = pushNotificationService.push(
                    target,
                    Notification.builder()
                            .setBody(notification.getContent())
                            .setTitle(notification.getTitle())
                            .setImage(notification.getImageUrl())
                            .build(),
                    null
            );
            logger.debug(
                    "Handle[processClassroomLeaving] - Push batch response - SuccessCount[{}] - FailureCount[{}] - Responses[{}]",
                    batchResponse.getSuccessCount(), batchResponse.getFailureCount(), batchResponse.getResponses()
            );
        } catch (Exception e) {
            logger.warn(
                    "Handle[processClassroomLeaving] - Catch exception while pushing notification - UserId[{}] - Title[{}] - Body[{}] - ImageUrl[{}] - Message[{}]",
                    target, notification.getTitle(), notification.getContent(), notification.getImageUrl(), e.getMessage()
            );
        }
    }

    @Async
    public void processNewPost(Post savedPost) {
        var classroom = savedPost.getClassroom();
        var members = new ArrayList<User>();
        members.add(classroom.getOwner());
        members.addAll(classroom.getUsers());
        members.addAll(classroom.getProviders());
        var memberIds = members.stream().map(User::getId).toList();

        var notifications = notificationService.createNewPostNotifications(members, savedPost);

        socketIOServer.getNamespace("/notification")
                .getAllClients()
                .stream()
                .filter(client -> {
                    String authId = client.getHandshakeData().getHttpHeaders().get("x-auth-id");
                    return memberIds.contains(authId);
                })
                .forEach(client -> {
                    client.sendEvent("notification:create");
                });

        var notification = notifications.get(0);

        try {
            var batchResponse = pushNotificationService.push(
                    memberIds,
                    Notification.builder()
                            .setBody(notification.getContent())
                            .setTitle(notification.getTitle())
                            .setImage(notification.getImageUrl())
                            .build(),
                    null
            );
            logger.debug(
                    "Handle[processNewPost] - Push batch response - SuccessCount[{}] - FailureCount[{}] - Responses[{}]",
                    batchResponse.getSuccessCount(), batchResponse.getFailureCount(), batchResponse.getResponses()
            );
        } catch (Exception e) {
            logger.warn(
                    "Handle[processNewPost] - Catch exception while pushing notification - UserId[{}] - Title[{}] - Body[{}] - ImageUrl[{}] - Message[{}]",
                    memberIds, notification.getTitle(), notification.getContent(), notification.getImageUrl(), e.getMessage()
            );
        }
    }


    @Async
    public void processNewComment(Comment savedComment) {
        var classroom = savedComment.getPost().getClassroom();
        var members = new ArrayList<User>();
        members.add(classroom.getOwner());
        members.addAll(classroom.getUsers());
        members.addAll(classroom.getProviders());
        var memberIds = members.stream().map(User::getId).toList();

        var notifications = notificationService.createNewCommentNotifications(members, savedComment);

        socketIOServer.getNamespace("/notification")
                .getAllClients()
                .stream()
                .filter(client -> {
                    String authId = client.getHandshakeData().getHttpHeaders().get("x-auth-id");
                    return memberIds.contains(authId);
                })
                .forEach(client -> {
                    client.sendEvent("notification:create");
                });

        var notification = notifications.get(0);

        try {
            var batchResponse = pushNotificationService.push(
                    memberIds,
                    Notification.builder()
                            .setBody(notification.getContent())
                            .setTitle(notification.getTitle())
                            .setImage(notification.getImageUrl())
                            .build(),
                    null
            );
            logger.debug(
                    "Handle[processNewComment] - Push batch response - SuccessCount[{}] - FailureCount[{}] - Responses[{}]",
                    batchResponse.getSuccessCount(), batchResponse.getFailureCount(), batchResponse.getResponses()
            );
        } catch (Exception e) {
            logger.warn(
                    "Handle[processNewComment] - Catch exception while pushing notification - UserId[{}] - Title[{}] - Body[{}] - ImageUrl[{}] - Message[{}]",
                    memberIds, notification.getTitle(), notification.getContent(), notification.getImageUrl(), e.getMessage()
            );
        }
    }


    @Async
    public void processNewClasswork(Classwork savedClasswork) {
        //TODO: implement
        var classroom = savedClasswork.getClassroom();
        var assignees = savedClasswork.getAssignees();
        var author = savedClasswork.getAuthor();

        CompletableFuture<Void> createNotificationsFuture = CompletableFuture.runAsync(() -> {

        });

        CompletableFuture<Void> sendMailFuture = CompletableFuture.runAsync(() -> {

        });

        CompletableFuture.allOf(createNotificationsFuture, sendMailFuture).join();
    }
}
