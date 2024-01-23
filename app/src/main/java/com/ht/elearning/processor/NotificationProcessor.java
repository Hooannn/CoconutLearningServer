package com.ht.elearning.processor;

import com.corundumstudio.socketio.SocketIOServer;
import com.google.firebase.messaging.Notification;
import com.ht.elearning.assignment.Assignment;
import com.ht.elearning.assignment.AssignmentSchedule;
import com.ht.elearning.assignment.AssignmentScheduleRepository;
import com.ht.elearning.assignment.AssignmentScheduleService;
import com.ht.elearning.classroom.Classroom;
import com.ht.elearning.classwork.Classwork;
import com.ht.elearning.comment.Comment;
import com.ht.elearning.comment.CommentRepository;
import com.ht.elearning.invitation.Invitation;
import com.ht.elearning.mail.MailService;
import com.ht.elearning.notification.NotificationService;
import com.ht.elearning.post.Post;
import com.ht.elearning.push_notification.PushNotificationService;
import com.ht.elearning.user.User;
import com.ht.elearning.user.UserRepository;
import com.ht.elearning.utils.Helper;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class NotificationProcessor {
    private static final Logger logger = LoggerFactory.getLogger(NotificationProcessor.class);
    private final MailService mailService;
    private final SocketIOServer socketIOServer;
    private final UserRepository userRepository;
    private final AssignmentScheduleService assignmentScheduleService;
    private final NotificationService notificationService;
    private final PushNotificationService pushNotificationService;
    @Value("${client.web.url}")
    private String clientWebUrl;

    @Async
    public void invitationDidCreate(Invitation invitation, Classroom classroom) {
        var urlString = clientWebUrl + "/classroom/invitation?invite_code=" + classroom.getInviteCode();
        URL acceptUrl = null;
        var user = userRepository.findByEmail(invitation.getEmail()).orElse(null);
        try {
            acceptUrl = new URI(urlString).toURL();

            CompletableFuture<Void> createNotificationFuture = CompletableFuture.runAsync(() -> {
                if (user != null) {
                    var notification = notificationService.createClassroomInvitation(user, classroom, invitation);
                    var target = user.getId();
                    HashMap<String, String> eventData = new HashMap<>();
                    eventData.put("notification_id", notification.getId());
                    notifySocket(target, "notification:created", eventData);

                    try {
                        var batchResponse = pushNotificationService.push(
                                target,
                                Notification.builder()
                                        .setBody(notification.getContent())
                                        .setTitle(notification.getTitle())
                                        .setImage(notification.getImageUrl())
                                        .build(),
                                new HashMap<>()
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
                String mailSubject = "Coconut - Classroom invitation";
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
    public void invitationsDidCreate(List<Invitation> invitations, Classroom classroom) {
        var urlString = clientWebUrl + "/classroom/invitation?invite_code=" + classroom.getInviteCode();
        URL acceptUrl = null;
        var users = userRepository.findAllByEmailIn(invitations.stream().map(Invitation::getEmail).toList());
        var emailsToSend = invitations.stream().map(Invitation::getEmail).toList();
        try {
            acceptUrl = new URI(urlString).toURL();

            CompletableFuture<Void> createNotificationFuture = CompletableFuture.runAsync(() -> {
                if (!users.isEmpty()) {
                    var notifications = notificationService.createClassroomInvitations(users, classroom, invitations.get(0));
                    var notification = notifications.get(0);
                    var target = users.stream().map(User::getId).toList();
                    notifySocket(target, "notification:created", new HashMap<>());

                    try {
                        var batchResponse = pushNotificationService.push(
                                target,
                                Notification.builder()
                                        .setBody(notification.getContent())
                                        .setTitle(notification.getTitle())
                                        .setImage(notification.getImageUrl())
                                        .build(),
                                new HashMap<>()
                        );
                        logger.debug(
                                "Handle[processClassroomInvitation] - Push batch response - SuccessCount[{}] - FailureCount[{}] - Responses[{}]",
                                batchResponse.getSuccessCount(), batchResponse.getFailureCount(), batchResponse.getResponses()
                        );
                    } catch (Exception e) {
                        logger.warn(
                                "Handle[processClassroomInvitation] - Catch exception while pushing notification - UserIds[{}] - Title[{}] - Body[{}] - ImageUrl[{}] - Message[{}]",
                                target, notification.getTitle(), notification.getContent(), notification.getImageUrl(), e.getMessage()
                        );
                    }
                }
            });
            URL finalAcceptUrl = acceptUrl;
            CompletableFuture<Void> sendMailFuture = CompletableFuture.runAsync(() -> {
                String mailSubject = "Coconut - Classroom invitation";
                mailService.sendClassroomInvitationMails(emailsToSend, mailSubject, finalAcceptUrl.toString());
            });

            CompletableFuture.allOf(createNotificationFuture, sendMailFuture).join();
        } catch (MalformedURLException | URISyntaxException e) {
            logger.error(
                    "Error while doing processClassroomInvitations - Emails[{}] - ClassroomId[{}] - Message[{}]",
                    emailsToSend,
                    classroom.getId(),
                    e.getMessage()
            );
        }
    }

    @Async
    public void memberDidJoin(Classroom classroom, User user) {
        var notification = notificationService.createMemberDidJoinNotification(user, classroom);
        var target = classroom.getOwner().getId();
        HashMap<String, String> eventData = new HashMap<>();
        eventData.put("notification_id", notification.getId());
        notifySocket(target, "notification:created", eventData);

        try {
            var batchResponse = pushNotificationService.push(
                    target,
                    Notification.builder()
                            .setBody(notification.getContent())
                            .setTitle(notification.getTitle())
                            .setImage(notification.getImageUrl())
                            .build(),
                    new HashMap<>()
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
    public void memberDidLeave(Classroom classroom, User user) {
        var notification = notificationService.createMemberDidLeaveNotification(user, classroom);
        var target = classroom.getOwner().getId();
        HashMap<String, String> eventData = new HashMap<>();
        eventData.put("notification_id", notification.getId());
        notifySocket(target, "notification:created", eventData);

        try {
            var batchResponse = pushNotificationService.push(
                    target,
                    Notification.builder()
                            .setBody(notification.getContent())
                            .setTitle(notification.getTitle())
                            .setImage(notification.getImageUrl())
                            .build(),
                    new HashMap<>()
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
    public void postDidCreate(Post savedPost) {
        var classroom = savedPost.getClassroom();
        var members = classroom.getMembers().stream().filter(m -> !m.getId().equals(savedPost.getAuthor().getId())).toList();
        var memberIds = members.stream().map(User::getId).toList();

        var notifications = notificationService.createPostDidCreateNotifications(members, savedPost);
        notifySocket(memberIds, "notification:created", new HashMap<>());

        var notification = notifications.get(0);

        try {
            var batchResponse = pushNotificationService.push(
                    memberIds,
                    Notification.builder()
                            .setBody(notification.getContent())
                            .setTitle(notification.getTitle())
                            .setImage(notification.getImageUrl())
                            .build(),
                    new HashMap<>()
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
    public void commentDidCreate(Comment savedComment) {
        var recipient = savedComment.getPost() == null ?
                savedComment.getClasswork().getAuthor() :
                savedComment.getPost().getAuthor();

        if (recipient.getId().equals(savedComment.getAuthor().getId())) return;

        var notifications = notificationService.createCommentDidCreateNotifications(List.of(recipient), savedComment);

        notifySocket(recipient.getId(), "notification:created", new HashMap<>());

        var notification = notifications.get(0);

        try {
            var batchResponse = pushNotificationService.push(
                    recipient.getId(),
                    Notification.builder()
                            .setBody(notification.getContent())
                            .setTitle(notification.getTitle())
                            .setImage(notification.getImageUrl())
                            .build(),
                    new HashMap<>()
            );
            logger.debug(
                    "Handle[processNewComment] - Push batch response - SuccessCount[{}] - FailureCount[{}] - Responses[{}]",
                    batchResponse.getSuccessCount(), batchResponse.getFailureCount(), batchResponse.getResponses()
            );
        } catch (Exception e) {
            logger.warn(
                    "Handle[processNewComment] - Catch exception while pushing notification - UserId[{}] - Title[{}] - Body[{}] - ImageUrl[{}] - Message[{}]",
                    recipient.getId(), notification.getTitle(), notification.getContent(), notification.getImageUrl(), e.getMessage()
            );
        }
    }


    @Async
    public void classworkDidCreate(Classwork savedClasswork) {
        var classroom = savedClasswork.getClassroom();
        var author = savedClasswork.getAuthor();

        //Create notification for assignees and other providers
        CompletableFuture<Void> createNotificationsFuture = CompletableFuture.runAsync(() -> {
            Set<User> providers = classroom.getProviders();
            Set<User> assignees = savedClasswork.getAssignees();
            List<User> recipients = Stream.concat(providers.stream(), assignees.stream())
                    .filter(r -> !r.getId().equals(author.getId()))
                    .toList();


            var notifications = notificationService.createClassworkDidCreateNotifications(recipients, savedClasswork);
            var notification = notifications.get(0);
            var recipientIds = recipients.stream().map(User::getId).toList();
            notifySocket(recipientIds, "notification:created", new HashMap<>());

            try {
                var batchResponse = pushNotificationService.push(
                        recipientIds,
                        Notification.builder()
                                .setBody(notification.getContent())
                                .setTitle(notification.getTitle())
                                .setImage(notification.getImageUrl())
                                .build(),
                        new HashMap<>()
                );
                logger.debug(
                        "Handle[processNewClasswork] - Push batch response - SuccessCount[{}] - FailureCount[{}] - Responses[{}]",
                        batchResponse.getSuccessCount(), batchResponse.getFailureCount(), batchResponse.getResponses()
                );
            } catch (Exception e) {
                logger.warn(
                        "Handle[processNewClasswork] - Catch exception while pushing notification - UserId[{}] - Title[{}] - Body[{}] - ImageUrl[{}] - Message[{}]",
                        recipientIds, notification.getTitle(), notification.getContent(), notification.getImageUrl(), e.getMessage()
                );
            }
        });

        //Just send mail for assignees
        CompletableFuture<Void> sendMailFuture = CompletableFuture.runAsync(() -> {
            var assignees = savedClasswork.getAssignees();
            String mailSubject = "Elearning - Classroom: " + savedClasswork.getClassroom().getName();
            assignees.forEach(assignee -> {
                try {
                    mailService.sendNewClassworkMail(assignee.getEmail(), mailSubject, savedClasswork);
                } catch (MessagingException e) {
                    logger.warn(
                            "Handle[processNewClasswork] - Catch exception while sending mail - To[{}] - Subject[{}] - ClassworkId[{}]",
                            assignee.getEmail(), mailSubject, savedClasswork.getId()
                    );
                }
            });
        });

        //Create schedule for reminder
        CompletableFuture<Void> createSchedulesFuture = CompletableFuture.runAsync(() -> {
            var deadline = savedClasswork.getDeadline();
            if (deadline == null) return;
            var assignees = savedClasswork.getAssignees();
            Date scheduledTime = new Date(deadline.getTime() - 24 * 60 * 60 * 1000);

            // Don't create schedule if scheduled time is today
            if (Helper.isSameDay(scheduledTime, new Date())) return;

            var schedules = assignees.stream().map(assignee -> AssignmentSchedule.builder()
                    .scheduledTime(scheduledTime)
                    .user(assignee)
                    .classwork(savedClasswork)
                    .build()
            ).toList();
            assignmentScheduleService.saveAll(schedules);
        });

        CompletableFuture.allOf(createNotificationsFuture, sendMailFuture, createSchedulesFuture).join();
    }


    @Async
    public void classworkDeadlineDidUpdate(Classwork savedClasswork) {
        var deadline = savedClasswork.getDeadline();
        if (deadline == null) {
            assignmentScheduleService.updateRemindedByClassworkId(true, savedClasswork.getId());
            return;
        }
        Date scheduledTime = new Date(deadline.getTime() - 24 * 60 * 60 * 1000);

        assignmentScheduleService.updateScheduledTimeByClassworkId(scheduledTime, savedClasswork.getId());

        var schedules = assignmentScheduleService.findUnremindedSchedulesByClassworkIdForToday(savedClasswork.getId());
        assignmentScheduleService.remind(schedules, logger);
    }


    @Async
    public void assignmentDidCreate(Assignment savedAssignment) {
        var recipient = savedAssignment.getClasswork().getAuthor();
        var notification = notificationService.createAssignmentDidCreateNotification(recipient, savedAssignment);
        notifySocket(recipient.getId(), "notification:created", new HashMap<>());
        try {
            var batchResponse = pushNotificationService.push(
                    recipient.getId(),
                    Notification.builder()
                            .setBody(notification.getContent())
                            .setTitle(notification.getTitle())
                            .setImage(notification.getImageUrl())
                            .build(),
                    new HashMap<>()
            );
            logger.debug(
                    "Handle[processNewComment] - Push batch response - SuccessCount[{}] - FailureCount[{}] - Responses[{}]",
                    batchResponse.getSuccessCount(), batchResponse.getFailureCount(), batchResponse.getResponses()
            );
        } catch (Exception e) {
            logger.warn(
                    "Handle[processNewComment] - Catch exception while pushing notification - UserId[{}] - Title[{}] - Body[{}] - ImageUrl[{}] - Message[{}]",
                    recipient.getId(), notification.getTitle(), notification.getContent(), notification.getImageUrl(), e.getMessage()
            );
        }
    }


    @Async
    public void classroomDidUpdate(Classroom classroom, ClassroomUpdateType updateType) {
        var memberIds = classroom.getMembers().stream().map(User::getId).toList();
        HashMap<String, String> eventData = new HashMap<>();
        eventData.put("type", String.valueOf(updateType));
        eventData.put("classroom_id", classroom.getId());
        notifySocket(memberIds, "classroom:updated", eventData);
    }


    @Async
    public void gradeDidCreate(Assignment savedAssignment) {
        var student = savedAssignment.getAuthor();
        var notification = notificationService.createGradeDidCreateNotification(student, savedAssignment);
        notifySocket(student.getId(), "notification:created", new HashMap<>());
        try {
            var batchResponse = pushNotificationService.push(
                    student.getId(),
                    Notification.builder()
                            .setBody(notification.getContent())
                            .setTitle(notification.getTitle())
                            .setImage(notification.getImageUrl())
                            .build(),
                    new HashMap<>()
            );
            logger.debug(
                    "Handle[gradeDidCreate] - Push batch response - SuccessCount[{}] - FailureCount[{}] - Responses[{}]",
                    batchResponse.getSuccessCount(), batchResponse.getFailureCount(), batchResponse.getResponses()
            );
        } catch (Exception e) {
            logger.warn(
                    "Handle[gradeDidCreate] - Catch exception while pushing notification - UserId[{}] - Title[{}] - Body[{}] - ImageUrl[{}] - Message[{}]",
                    student.getId(), notification.getTitle(), notification.getContent(), notification.getImageUrl(), e.getMessage()
            );
        }
    }


    private void notifySocket(List<String> memberIds, String event, HashMap<String, String> eventData) {
        socketIOServer.getNamespace("/notification")
                .getAllClients()
                .stream()
                .filter(client -> memberIds.contains(client.getHandshakeData().getHttpHeaders().get("x-auth-id")))
                .forEach(client -> {
                    client.sendEvent(event, eventData);
                    logger.debug("[Socket] Send Event[{}] - EventData[{}] - UserId[{}]",
                            event,
                            eventData,
                            client.getHandshakeData().getHttpHeaders().get("x-auth-id")
                    );
                });
    }


    private void notifySocket(String memberId, String event, HashMap<String, String> eventData) {
        socketIOServer.getNamespace("/notification")
                .getAllClients()
                .stream()
                .filter(client -> client.getHandshakeData().getHttpHeaders().get("x-auth-id").equals(memberId))
                .findFirst()
                .ifPresent(client -> {
                    client.sendEvent(event, eventData);
                    logger.debug("[Socket] Send Event[{}] - EventData[{}] - UserId[{}]",
                            event,
                            eventData,
                            client.getHandshakeData().getHttpHeaders().get("x-auth-id")
                    );
                });
    }
}