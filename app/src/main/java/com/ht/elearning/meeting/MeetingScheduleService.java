package com.ht.elearning.meeting;

import com.google.firebase.messaging.Notification;
import com.ht.elearning.assignment.AssignmentSchedule;
import com.ht.elearning.mail.MailService;
import com.ht.elearning.notification.NotificationService;
import com.ht.elearning.push_notification.PushNotificationService;
import com.ht.elearning.user.User;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MeetingScheduleService {
    private final MeetingScheduleRepository meetingScheduleRepository;
    private final NotificationService notificationService;
    private final MailService mailService;
    private final PushNotificationService pushNotificationService;

    public List<MeetingSchedule> createMany(Meeting meeting, Set<User> users, Date scheduledTime) {
        var schedules = users.stream().map(u -> MeetingSchedule.builder()
                .scheduledTime(scheduledTime)
                .user(u)
                .meeting(meeting)
                .build()
        ).toList();
        return meetingScheduleRepository.saveAll(schedules);
    }

    public void remind(List<MeetingSchedule> schedules, Logger logger) {
        schedules.forEach(schedule -> remind(schedule, logger));
    }

    public void remind(MeetingSchedule schedule, Logger logger) {
        logger.info("Reminding assignment ClassroomId[{}] - MeetingId[{}] - UserId[{}]",
                schedule.getMeeting().getClassroom().getId(),
                schedule.getMeeting().getId(),
                schedule.getUser().getId());

        var notification = notificationService.createMeetingReminderNotification(schedule.getUser(), schedule);

        try {
            if (schedule.getUser().isEnabledEmailNotification())
                mailService.sendMeetingReminderMail(schedule.getUser().getEmail(), "Coconut - Meeting reminder", schedule.getMeeting());

            if (schedule.getUser().isEnabledPushNotification()) {
                var batchResponse = pushNotificationService.push(
                        schedule.getUser().getId(),
                        Notification.builder()
                                .setBody(notification.getContent())
                                .setTitle(notification.getTitle())
                                .setImage(notification.getImageUrl())
                                .build(),
                        new HashMap<>()
                );
                logger.debug(
                        "Handle[remind] - Push batch response - SuccessCount[{}] - FailureCount[{}] - Responses[{}]",
                        batchResponse.getSuccessCount(), batchResponse.getFailureCount(), batchResponse.getResponses()
                );
            }
        } catch (MessagingException e) {
            logger.error("Error sending mail to - ${} - Message[{}]", schedule.getUser().getEmail(), e.getMessage());
        } catch (Exception e) {
            logger.warn(
                    "Handle[remind] - Catch exception while pushing notification - UserId[{}] - Title[{}] - Body[{}] - ImageUrl[{}] - Message[{}]",
                    schedule.getUser().getId(), notification.getTitle(), notification.getContent(), notification.getImageUrl(), e.getMessage()
            );
        }

        schedule.setReminded(true);
        meetingScheduleRepository.save(schedule);
    }

    public void deleteAllByMeetingId(String classworkId) {
        meetingScheduleRepository.deleteAllByMeetingId(classworkId);
    }

    public void deleteAllByScheduledTimeBeforeOrRemindedIsTrue(Date d) {
        meetingScheduleRepository.deleteAllByScheduledTimeBeforeOrRemindedIsTrue(d);
    }

    public List<MeetingSchedule> findUnremindedSchedulesForToday() {
        return meetingScheduleRepository.findUnremindedSchedulesForToday();
    }

    public List<MeetingSchedule> findUnremindedSchedulesByMeetingIdForToday(String meetingId) {
        return meetingScheduleRepository.findUnremindedSchedulesByMeetingIdForToday(meetingId);
    }
}
