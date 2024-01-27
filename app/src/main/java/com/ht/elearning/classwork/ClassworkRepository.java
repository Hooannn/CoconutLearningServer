package com.ht.elearning.classwork;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ClassworkRepository extends JpaRepository<Classwork, String> {
    <T> Optional<T> findById(String id, Class<T> type);

    List<Classwork> findAllByClassroomId(String classroomId);

    List<Classwork> findAllByClassroomIdAndDeadlineBetweenOrderByDeadlineAsc(String classroomIdd, Date startDate, Date endDate);

    boolean existsByIdAndClassroomId(String classworkId, String classroomId);

    boolean existsByIdAndAssigneesId(String classworkId, String userId);

    List<Classwork> findAllByClassroomIdAndAssigneesIdAndDeadlineBetweenOrderByDeadlineAsc(String classroomId, String assigneeId, Date startDate, Date endDate);

    <T> List<T> findAllByClassroomIdAndAssigneesId(String classroomId, String userId, Class<T> type);

    List<Classwork> findAllByAssigneesIdAndDeadlineIsBetweenAndTypeIsOrderByDeadlineAsc(String userId, Date startDate, Date endDate, ClassworkType cT);

    List<Classwork> findAllByClassroomProvidersIdAndTypeIsOrderByDeadlineAsc(String userId, ClassworkType type);

    List<Classwork> findAllByClassroomProvidersIdAndDeadlineIsBetweenAndTypeIsOrderByDeadlineAsc(String userId, Date startDate, Date endDate, ClassworkType type);

    <T> List<T> findAllByAssignmentsAuthorIdAndAssignmentsSubmittedFalseOrAssignmentsSubmittedIsNullAndTypeIs(String userId, ClassworkType cT, Class<T> type);

    <T> List<T> findAllByAssignmentsAuthorIdAndAssignmentsSubmittedTrueAndTypeIs(String userId, ClassworkType cT, Class<T> type);
}
