package com.ht.elearning.assignment;

import com.google.firebase.messaging.Notification;
import com.ht.elearning.mail.MailService;
import com.ht.elearning.notification.NotificationService;
import com.ht.elearning.push_notification.PushNotificationService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;

@Service
@RequiredArgsConstructor
public class AssignmentScheduleService {
    private final AssignmentScheduleRepository assignmentScheduleRepository;
    private final NotificationService notificationService;
    private final MailService mailService;
    private final PushNotificationService pushNotificationService;

    public List<AssignmentSchedule> saveAll(List<AssignmentSchedule> schedules) {
        return assignmentScheduleRepository.saveAll(schedules);
    }

    public void updateScheduledTimeByClassworkId(Date scheduledTime, String classworkId) {
        assignmentScheduleRepository.updateScheduledTimeByClassworkId(scheduledTime, classworkId);
    }

    public void updateRemindedByClassworkId(boolean reminded, String classworkId) {
        assignmentScheduleRepository.updateRemindedByClassworkId(reminded, classworkId);
    }

    public void deleteAllByScheduledTimeBeforeOrRemindedIsTrue(Date d) {
        assignmentScheduleRepository.deleteAllByScheduledTimeBeforeOrRemindedIsTrue(d);
    }

    public List<AssignmentSchedule> findUnremindedSchedulesForToday() {
        return assignmentScheduleRepository.findUnremindedSchedulesForToday();
    }

    public List<AssignmentSchedule> findUnremindedSchedulesByClassworkIdForToday(String classworkId) {
        return assignmentScheduleRepository.findUnremindedSchedulesByClassworkIdForToday(classworkId);
    }

    public void remind(List<AssignmentSchedule> schedules, Logger logger) {
        schedules.forEach(schedule -> remind(schedule, logger));
    }

    public void remind(AssignmentSchedule schedule, Logger logger) {
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
    }

    public void deleteAllByClassworkId(String classworkId) {
        assignmentScheduleRepository.deleteAllByClassworkId(classworkId);
    }
}
