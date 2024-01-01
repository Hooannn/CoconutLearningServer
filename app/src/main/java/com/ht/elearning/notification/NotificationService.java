package com.ht.elearning.notification;

import com.ht.elearning.classroom.Classroom;
import com.ht.elearning.user.User;
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


    public Notification createClassroomInvitation(User user, Classroom classroom) {
        var urlString = "https://example.com/classroom/invitation?invite_code=" + classroom.getInviteCode();

        List<Action> actions = List.of(
                Action.builder().title("Accept").description(null).callbackUrl("/api/v1/classrooms/join/" + classroom.getInviteCode()).build(),
                Action.builder().title("Refuse").description(null).callbackUrl("/api/v1/classrooms/refuse/" + classroom.getInviteCode()).build()
        );

        Notification notification = Notification.builder()
                .title("Invitation")
                .content("You are invited to join the class: '" + classroom.getName() + "'")
                .redirectUrl(urlString)
                .recipient(user)
                .actions(actions)
                .imageUrl(classroom.getOwner().getAvatarUrl())
                .build();

        return repository.save(notification);
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

        return repository.save(notification);
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

        return repository.save(notification);
    }


    public List<Notification> createNewPostNotifications(List<User> recipients, User author, Classroom classroom) {
        var urlString = "https://example.com/classroom/" + classroom.getId();

        List<Notification> notifications = recipients.stream().map(
                recipient -> Notification.builder()
                        .title("Classroom: '" + classroom.getName() + "'")
                        .content(author.getFullName() + " created a new post.")
                        .redirectUrl(urlString)
                        .recipient(recipient)
                        .imageUrl(author.getAvatarUrl())
                        .build()
        ).toList();

        return repository.saveAll(notifications);
    }
}
