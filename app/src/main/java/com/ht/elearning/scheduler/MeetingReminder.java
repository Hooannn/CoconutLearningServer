package com.ht.elearning.scheduler;

import com.ht.elearning.meeting.MeetingScheduleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MeetingReminder {
    private static final Logger logger = LoggerFactory.getLogger(MeetingReminder.class);
    private final MeetingScheduleService meetingScheduleService;

    //Run at 00:00 and 12:00 everyday
    @Scheduled(cron = "0 0 0,12 * * ?")
    public void remind() {
        var schedules = meetingScheduleService.findUnremindedSchedulesForToday();
        meetingScheduleService.remind(schedules, logger);
    }

    //Run at 00:00 on the first day of every month
    @Scheduled(cron = "0 0 0 1 * ?")
    public void clearSchedules() {
        logger.info("Clearing reminded meeting schedules or schedules that are scheduled in the past");
        meetingScheduleService.deleteAllByScheduledTimeBeforeOrRemindedIsTrue(new java.util.Date());
    }
}
