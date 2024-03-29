package com.ht.elearning.notification;

import com.ht.elearning.assignment.Assignment;
import com.ht.elearning.assignment.AssignmentSchedule;
import com.ht.elearning.classroom.Classroom;
import com.ht.elearning.classwork.Classwork;
import com.ht.elearning.comment.Comment;
import com.ht.elearning.config.HttpException;
import com.ht.elearning.constants.ErrorMessage;
import com.ht.elearning.invitation.Invitation;
import com.ht.elearning.invitation.InvitationType;
import com.ht.elearning.meeting.Meeting;
import com.ht.elearning.meeting.MeetingSchedule;
import com.ht.elearning.post.Post;
import com.ht.elearning.user.User;
import com.ht.elearning.utils.QueryPair;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Locale;


@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;


    public QueryPair<Notification> findMyNotifications(String userId, int skip, int limit) {
        PageRequest pageable = PageRequest.of(skip, limit);
        Long total = notificationRepository.countByRecipientId(userId);
        var notifications = notificationRepository.findAllByRecipientIdOrderByCreatedAtDesc(userId, pageable);
        return new QueryPair<>(notifications, total);
    }


    public long countUnread(String userId) {
        return notificationRepository.countByRecipientIdAndReadFalse(userId);
    }


    public Notification createClassroomInvitation(User user, Classroom classroom, Invitation invitation) {
        List<Action> actions = List.of(
                Action.builder().type(ActionType.PRIMARY).title("Accept").description(null).callbackUrl("/api/v1/classrooms/accept/" + classroom.getInviteCode()).build(),
                Action.builder().type(ActionType.DANGER).title("Refuse").description(null).callbackUrl("/api/v1/classrooms/refuse/" + classroom.getInviteCode()).build()
        );

        String message = invitation.getType() == InvitationType.PROVIDER ?
                "You are invited to teach the class: '" + classroom.getName() + "'"
                :
                "You are invited to join the class: '" + classroom.getName() + "'";
        Notification notification = Notification.builder()
                .title("Invitation")
                .content(message)
                .redirectUrl(null)
                .recipient(user)
                .actions(actions)
                .imageUrl(classroom.getOwner().getAvatarUrl())
                .build();

        return notificationRepository.save(notification);
    }


    public List<Notification> createClassroomInvitations(List<User> users, Classroom classroom, Invitation invitation) {
        List<Action> actions = List.of(
                Action.builder().type(ActionType.PRIMARY).title("Accept").description(null).callbackUrl("/api/v1/classrooms/webhook/accept/" + classroom.getInviteCode()).build(),
                Action.builder().type(ActionType.DANGER).title("Refuse").description(null).callbackUrl("/api/v1/classrooms/webhook/refuse/" + classroom.getInviteCode()).build()
        );

        String message = invitation.getType() == InvitationType.PROVIDER ?
                "You are invited to teach the class: '" + classroom.getName() + "'"
                :
                "You are invited to join the class: '" + classroom.getName() + "'";

        List<Notification> notifications = users.stream().map(user -> Notification.builder()
                .title("Invitation")
                .content(message)
                .redirectUrl(null)
                .recipient(user)
                .actions(actions)
                .imageUrl(classroom.getOwner().getAvatarUrl())
                .build()).toList();

        return notificationRepository.saveAll(notifications);
    }


    public Notification createMemberDidJoinNotification(User user, Classroom classroom) {
        var urlString = "/classroom/" + classroom.getId() + "?tab=members";
        Notification notification = Notification.builder()
                .title("Classroom: '" + classroom.getName() + "'")
                .content(user.getFullName() + " joined.")
                .redirectUrl(urlString)
                .recipient(classroom.getOwner())
                .imageUrl(user.getAvatarUrl())
                .build();

        return notificationRepository.save(notification);
    }


    public Notification createMemberDidLeaveNotification(User user, Classroom classroom) {
        var urlString = "/classroom/" + classroom.getId() + "?tab=members";

        Notification notification = Notification.builder()
                .title("Classroom: '" + classroom.getName() + "'")
                .content(user.getFullName() + " left.")
                .redirectUrl(urlString)
                .recipient(classroom.getOwner())
                .imageUrl(user.getAvatarUrl())
                .build();

        return notificationRepository.save(notification);
    }


    public List<Notification> createPostDidCreateNotifications(List<User> recipients, Post savedPost) {
        var classroom = savedPost.getClassroom();
        var author = savedPost.getAuthor();
        var urlString = "/classroom/" + classroom.getId();

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


    public List<Notification> createCommentDidCreateNotifications(List<User> recipients, Comment savedComment) {
        var classroom = savedComment.getPost() == null ?
                savedComment.getClasswork().getClassroom() :
                savedComment.getPost().getClassroom();

        var author = savedComment.getAuthor();

        var urlString = savedComment.getPost() == null ?
                "/classroom/" + classroom.getId() + "/classwork/" + savedComment.getClasswork().getId() :
                "/classroom/" + classroom.getId();

        List<Notification> notifications = recipients.stream().map(
                recipient -> Notification.builder()
                        .title("Classroom: '" + classroom.getName() + "'")
                        .content(author.getFullName() + " added new comment: " + "\"" + savedComment.getBody() + "\"")
                        .redirectUrl(urlString)
                        .recipient(recipient)
                        .imageUrl(author.getAvatarUrl())
                        .build()
        ).toList();

        return notificationRepository.saveAll(notifications);
    }


    public List<Notification> createClassworkDidCreateNotifications(List<User> recipients, Classwork savedClasswork) {
        var classroom = savedClasswork.getClassroom();
        var author = savedClasswork.getAuthor();
        var urlString = "/classroom/" + classroom.getId() + "/classwork/" + savedClasswork.getId();

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


    public Notification createAssignmentDidCreateNotification(User recipient, Assignment savedAssignment) {
        var classwork = savedAssignment.getClasswork();
        var classroom = classwork.getClassroom();
        var author = savedAssignment.getAuthor();
        var urlString = "/classroom/" + classroom.getId() + "/classwork/" + classwork.getId() + "?tab=student_assignments";

        Notification notification = Notification.builder()
                .title("Classroom: '" + classroom.getName() + "'")
                .content(author.getFullName() + " submitted new assignment")
                .redirectUrl(urlString)
                .recipient(recipient)
                .imageUrl(author.getAvatarUrl())
                .build();

        return notificationRepository.save(notification);
    }


    public Notification createGradeDidCreateNotification(User recipient, Assignment savedAssignment) {
        var classwork = savedAssignment.getClasswork();
        var classroom = classwork.getClassroom();
        var author = savedAssignment.getGrade().getGradedBy();
        var urlString = "/classroom/" + classroom.getId() + "/classwork/" + classwork.getId();

        Notification notification = Notification.builder()
                .title("Classroom: '" + classroom.getName() + "'")
                .content(author.getFullName() + " graded your assignment")
                .redirectUrl(urlString)
                .recipient(recipient)
                .imageUrl(author.getAvatarUrl())
                .build();

        return notificationRepository.save(notification);
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
                .orElseThrow(() -> new HttpException(ErrorMessage.NOTIFICATION_NOT_FOUND, HttpStatus.BAD_REQUEST));
        notification.setRead(true);
        notificationRepository.save(notification);
        return true;
    }


    public void markAsDone(String notificationId) {
        var notification = notificationRepository.findById(notificationId).orElse(null);
        if (notification != null) {
            notification.setRead(true);
            notification.setActions(null);
            notificationRepository.save(notification);
        }
    }


    public Notification createAssignmentReminderNotification(User recipient, AssignmentSchedule schedule) {
        var classwork = schedule.getClasswork();
        var classroom = classwork.getClassroom();
        var author = classwork.getAuthor();
        var urlString = "/classroom/" + classroom.getId() + "/classwork/" + classwork.getId();

        DateFormatter dateFormatter = new DateFormatter("dd/MM/yyyy HH:mm");
        var deadline = dateFormatter.print(classwork.getDeadline(), Locale.ENGLISH);
        Notification notification = Notification.builder()
                .title("Classroom: '" + classroom.getName() + "'")
                .content("You have an assignment due tomorrow.\n" + "\"" + classwork.getTitle() + "\"" + " - " + deadline)
                .redirectUrl(urlString)
                .recipient(recipient)
                .imageUrl(author.getAvatarUrl())
                .build();

        return notificationRepository.save(notification);
    }

    public List<Notification> createMeetingDidCreateNotifications(List<User> recipients, Meeting savedMeeting) {
        var classroom = savedMeeting.getClassroom();
        var author = savedMeeting.getCreatedBy();
        var urlString = "/classroom/" + classroom.getId() + "?tab=meeting";

        List<Notification> notifications = recipients.stream().map(
                recipient -> Notification.builder()
                        .title("Classroom: '" + classroom.getName() + "'")
                        .content(author.getFullName() + " scheduled new meeting: " + "\"" + savedMeeting.getName() + "\"")
                        .redirectUrl(urlString)
                        .recipient(recipient)
                        .imageUrl(author.getAvatarUrl())
                        .build()
        ).toList();

        return notificationRepository.saveAll(notifications);
    }

    public Notification createMeetingReminderNotification(User recipient, MeetingSchedule schedule) {
        var meeting = schedule.getMeeting();
        var classroom = meeting.getClassroom();
        var author = meeting.getCreatedBy();
        var urlString = "/classroom/" + classroom.getId() + "?tab=meeting";

        DateFormatter dateFormatter = new DateFormatter("dd/MM/yyyy HH:mm");
        var time = dateFormatter.print(meeting.getStartAt(), Locale.ENGLISH);
        Notification notification = Notification.builder()
                .title("Classroom: '" + classroom.getName() + "'")
                .content("You have an meeting tomorrow.\n" + "\"" + meeting.getName() + "\"" + " - " + time)
                .redirectUrl(urlString)
                .recipient(recipient)
                .imageUrl(author.getAvatarUrl())
                .build();

        return notificationRepository.save(notification);
    }
}
