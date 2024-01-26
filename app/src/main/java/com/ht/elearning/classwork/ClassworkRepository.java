package com.ht.elearning.classwork;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ClassworkRepository extends JpaRepository<Classwork, String> {
    <T> Optional<T> findById(String id, Class<T> type);

    List<Classwork> findAllByClassroomId(String classroomId);

    List<Classwork> findAllByClassroomIdAndDeadlineBetweenOrderByDeadlineAsc(String classroomIdd, Date startDate, Date endDate);

    <T> List<T> findAllByClassroomId(String classroomId, Class<T> type);

    boolean existsByIdAndClassroomId(String classworkId, String classroomId);

    boolean existsByIdAndAssigneesId(String classworkId, String userId);

    List<Classwork> findAllByClassroomIdAndAssigneesId(String classroomId, String userId);

    List<Classwork> findAllByClassroomIdAndAssigneesIdAndDeadlineBetweenOrderByDeadlineAsc(String classroomId, String assigneeId, Date startDate, Date endDate);

    <T> List<T> findAllByClassroomIdAndAssigneesId(String classroomId, String userId, Class<T> type);
}
