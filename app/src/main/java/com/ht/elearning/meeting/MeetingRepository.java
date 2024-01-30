package com.ht.elearning.meeting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, String> {
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM Meeting m WHERE (m.startAt <= ?1 AND m.endAt >= ?1) or (m.startAt <= ?2 AND m.endAt >= ?2)")
    boolean existsMeetingTime(Date startAt, Date endAt);

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM Meeting m WHERE m.id != ?3 and ((m.startAt <= ?1 AND m.endAt >= ?1) or (m.startAt <= ?2 AND m.endAt >= ?2))")
    boolean existsMeetingTime(Date startAt, Date endAt, String updatedMeetingId);

    Optional<Meeting> findByIdAndCreatedById(String meetingId, String userId);

    List<Meeting> findAllByClassroomIdAndEndAtAfterOrderByStartAtAsc(String classroomId, Date date);
}
