package com.ht.elearning.notification;

import com.ht.elearning.classroom.Classroom;
import com.ht.elearning.classwork.Classwork;
import com.ht.elearning.comment.Comment;
import com.ht.elearning.config.HttpException;
import com.ht.elearning.invitation.Invitation;
import com.ht.elearning.invitation.InvitationType;
import com.ht.elearning.post.Post;
import com.ht.elearning.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;


    public List<Notification> findMyNotifications(String userId) {
        return notificationRepository.findAllByRecipientId(userId);
    }


    public long countUnread(String userId) {
        return notificationRepository.countByRecipientIdAndReadFalse(userId);
    }


    public Notification createClassroomInvitation(User user, Classroom classroom, Invitation invitation) {
        var urlString = "https://example.com/classroom/invitation?invite_code=" + classroom.getInviteCode();

        List<Action> actions = List.of(
                Action.builder().title("Accept").description(null).callbackUrl("/api/v1/classrooms/accept/" + classroom.getInviteCode()).build(),
                Action.builder().title("Refuse").description(null).callbackUrl("/api/v1/classrooms/refuse/" + classroom.getInviteCode()).build()
        );

        String message = invitation.getType() == InvitationType.PROVIDER ?
                "You are invited to teach the class: '" + classroom.getName() + "'"
                :
                "You are invited to join the class: '" + classroom.getName() + "'";
        Notification notification = Notification.builder()
                .title("Invitation")
                .content(message)
                .redirectUrl(urlString)
                .recipient(user)
                .actions(actions)
                .imageUrl(classroom.getOwner().getAvatarUrl())
                .build();

        return notificationRepository.save(notification);
    }


    public Notification createJoiningClassroomNotification(User user, Classroom classroom) {
        var urlString = "https://example.com/classroom/" + classroom.getId();

        List<Action> actions = List.of(
                Action.builder().title("Remove").description(null).callbackUrl(null).build()
        );

        Notification notification = Notification.builder()
                .title("Classroom: '" + classroom.getName() + "'")
                .content(user.getFullName() + " joined.")
                .redirectUrl(urlString)
                .recipient(classroom.getOwner())
                .actions(actions)
                .imageUrl(user.getAvatarUrl())
                .build();

        return notificationRepository.save(notification);
    }


    public Notification createLeavingClassroomNotification(User user, Classroom classroom) {
        var urlString = "https://example.com/classroom/" + classroom.getId();

        Notification notification = Notification.builder()
                .title("Classroom: '" + classroom.getName() + "'")
                .content(user.getFullName() + " left.")
                .redirectUrl(urlString)
                .recipient(classroom.getOwner())
                .imageUrl(user.getAvatarUrl())
                .build();

        return notificationRepository.save(notification);
    }


    public List<Notification> createNewPostNotifications(List<User> recipients, Post savedPost) {
        var classroom = savedPost.getClassroom();
        var author = savedPost.getAuthor();
        var urlString = "https://example.com/classroom/" + classroom.getId();

        List<Notification> notifications = recipients.stream().map(
                recipient -> Notification.builder()
                        .title("Classroom: '" + classroom.getName() + "'")
                        .content(author.getFullName() + " created a new post: " + "\"" + savedPost.getBody() + "\"")
                        .redirectUrl(urlString)
                        .recipient(recipient)
                        .imageUrl(author.getAvatarUrl())
                        .build()
        ).toList();

        return notificationRepository.saveAll(notifications);
    }


    public List<Notification> createNewCommentNotifications(List<User> recipients, Comment savedComment) {
        var classroom = savedComment.getPost().getClassroom();
        var author = savedComment.getAuthor();
        var urlString = "https://example.com/classroom/" + classroom.getId();

        List<Notification> notifications = recipients.stream().map(
                recipient -> Notification.builder()
                        .title("Classroom: '" + classroom.getName() + "'")
                        .content(author.getFullName() + " commented to post: " + "\"" + savedComment.getBody() + "\"")
                        .redirectUrl(urlString)
                        .recipient(recipient)
                        .imageUrl(author.getAvatarUrl())
                        .build()
        ).toList();

        return notificationRepository.saveAll(notifications);
    }


    public List<Notification> createNewClassworkNotifications(List<User> recipients, Classwork savedClasswork) {
        var classroom = savedClasswork.getClassroom();
        var author = savedClasswork.getAuthor();
        var urlString = "https://example.com/classroom/classwork/" + savedClasswork.getId();

        List<Notification> notifications = recipients.stream().map(
                recipient -> Notification.builder()
                        .title("Classroom: '" + classroom.getName() + "'")
                        .content(author.getFullName() + " created new classwork: " + "\"" + savedClasswork.getTitle() + "\"")
                        .redirectUrl(urlString)
                        .recipient(recipient)
                        .imageUrl(author.getAvatarUrl())
                        .build()
        ).toList();

        return notificationRepository.saveAll(notifications);
    }


    public boolean deleteMyNotifications(String userId) {
        notificationRepository.deleteAllByRecipientId(userId);
        return true;
    }


    public boolean markAll(String userId) {
        notificationRepository.markNotificationsAsRead(userId);
        return true;
    }


    public boolean mark(String notificationId, String userId) {
        var notification = notificationRepository.findByIdAndRecipientId(notificationId, userId)
                .orElseThrow(() -> new HttpException("Notification not found", HttpStatus.BAD_REQUEST));
        notification.setRead(true);
        notificationRepository.save(notification);
        return true;
    }
}
