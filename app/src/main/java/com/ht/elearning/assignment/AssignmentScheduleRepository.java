package com.ht.elearning.assignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface AssignmentScheduleRepository extends JpaRepository<AssignmentSchedule, Long> {
    @Query(value = "select s.* from assignment_schedules s where s.reminded = false and date(s.scheduled_time) = current_date", nativeQuery = true)
    List<AssignmentSchedule> findUnremindedSchedulesForToday();

    @Query(value = "select s.* from assignment_schedules s where s.reminded = false and date(s.scheduled_time) = current_date and s.classwork_id = ?1", nativeQuery = true)
    List<AssignmentSchedule> findUnremindedSchedulesByClassworkIdForToday(String classworkId);

    List<AssignmentSchedule> findAllByClassworkIdAndRemindedIsFalse(String classworkId);

    @Modifying
    @Query(value = "update assignment_schedules s set s.scheduledTime = ?1, s.reminded = false where s.classwork_id = ?2", nativeQuery = true)
    void updateScheduledTimeByClassworkId(Date scheduledTime, String classworkId);

    void deleteAllByScheduledTimeBeforeOrRemindedIsTrue(Date scheduledTime);

    @Modifying
    @Query(value = "update assignment_schedules s set s.reminded = ?1 where s.classwork_id = ?2", nativeQuery = true)
    void updateRemindedByClassworkId(Boolean reminded, String classworkId);

    void deleteAllByClassworkId(String classworkId);
}
