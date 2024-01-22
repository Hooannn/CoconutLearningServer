package com.ht.elearning.scheduler;

import com.google.firebase.messaging.Notification;
import com.ht.elearning.assignment.AssignmentScheduleRepository;
import com.ht.elearning.mail.MailService;
import com.ht.elearning.notification.NotificationService;
import com.ht.elearning.push_notification.PushNotificationService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class AssignmentReminder {
    private static final Logger logger = LoggerFactory.getLogger(AssignmentReminder.class);
    private final AssignmentScheduleRepository assignmentScheduleRepository;
    private final NotificationService notificationService;
    private final PushNotificationService pushNotificationService;
    private final MailService mailService;

    @Scheduled(cron = "0 * * * * ?")
    public void remind() {
        var schedules = assignmentScheduleRepository.findUnremindedSchedulesForToday();
        schedules.forEach(schedule -> {
            logger.info("Reminding assignment ScheduleId[{}] - ClassroomId[{}] - ClassworkId[{}] - UserId[{}]",
                    schedule.getId(),
                    schedule.getClasswork().getClassroom().getId(),
                    schedule.getClasswork().getId(),
                    schedule.getUser().getId());

            var notification = notificationService.createAssignmentReminderNotification(schedule.getUser(), schedule);

            try {
                mailService.sendClassworkReminderMail(schedule.getUser().getEmail(), "Coconut - Classwork reminder", schedule.getClasswork());
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
            } catch (MessagingException e) {
                logger.error("Error sending mail to - ${} - Message[{}]", schedule.getUser().getEmail(), e.getMessage());
            } catch (Exception e) {
                logger.warn(
                        "Handle[remind] - Catch exception while pushing notification - UserId[{}] - Title[{}] - Body[{}] - ImageUrl[{}] - Message[{}]",
                        schedule.getUser().getId(), notification.getTitle(), notification.getContent(), notification.getImageUrl(), e.getMessage()
                );
            }

            schedule.setReminded(true);
            assignmentScheduleRepository.save(schedule);
        });
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    public void clearSchedules() {
        logger.info("Clearing reminded assignment schedules");
        assignmentScheduleRepository.deleteAllByRemindedIsTrue();
    }
}
