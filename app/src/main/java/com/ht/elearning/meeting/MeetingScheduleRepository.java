package com.ht.elearning.meeting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface MeetingScheduleRepository extends JpaRepository<MeetingSchedule, MeetingScheduleId> {
    void deleteAllByMeetingId(String meetingId);

    void deleteAllByScheduledTimeBeforeOrRemindedIsTrue(Date scheduledTime);

    @Query(value = "select s.* from meeting_schedules s where s.reminded = false and date(s.scheduled_time) = current_date", nativeQuery = true)
    List<MeetingSchedule> findUnremindedSchedulesForToday();

    @Query(value = "select s.* from meeting_schedules s where s.reminded = false and date(s.scheduled_time) = current_date and s.meeting_id = ?1", nativeQuery = true)
    List<MeetingSchedule> findUnremindedSchedulesByMeetingIdForToday(String meetingId);
}
