package com.ht.elearning.classwork;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ClassworkRepository extends JpaRepository<Classwork, String> {
    <T> Optional<T> findById(String id, Class<T> type);

    List<Classwork> findAllByClassroomId(String classroomId);

    List<Classwork> findAllByClassroomIdAndTypeIs(String classroomId, ClassworkType type);

    List<Classwork> findAllByClassroomIdAndDeadlineBetweenOrderByDeadlineAsc(String classroomIdd, Date startDate, Date endDate);

    boolean existsByIdAndClassroomId(String classworkId, String classroomId);

    boolean existsByIdAndAssigneesId(String classworkId, String userId);

    <T> List<T> findAllByClassroomIdAndAssigneesIdAndDeadlineBetweenOrderByDeadlineAsc(String classroomId, String assigneeId, Date startDate, Date endDate, Class<T> type);

    <T> List<T> findAllByClassroomIdAndAssigneesId(String classroomId, String userId, Class<T> type);

    <T> List<T> findAllByClassroomIdAndAssigneesIdAndTypeIs(String classroomId, String userId, ClassworkType classworkType, Class<T> type);

    List<Classwork> findAllByClassroomProvidersIdOrClassroomOwnerIdAndTypeIsOrderByDeadlineAsc(String userId, String userId1, ClassworkType type);

    List<Classwork> findAllByClassroomProvidersIdOrClassroomOwnerIdAndDeadlineIsBetweenAndTypeIsOrderByDeadlineAsc(String userId, String userId1, Date startDate, Date endDate, ClassworkType type);

    List<Classwork> findAllByClassroomProvidersIdOrClassroomOwnerIdAndDeadlineIsBetweenAndTypeIsAndClassroomIdOrderByDeadlineAsc(String userId, String userId1, Date startDate, Date endDate, ClassworkType type, String classroomId);

    @Query("""
             SELECT c FROM Classwork c left join c.assignees a left join c.assignments ass on ass.author.id = ?1
             where a.id = ?1
             and c.type = 'EXAM'
             and (ass.submitted is null or ass.submitted = false)
             and ass.grade is null
            """)
    <T> List<T> findTodoClassworkByUserId(String userId, Class<T> type);

    @Query("""
             SELECT c FROM Classwork c left join c.assignees a left join c.assignments ass on ass.author.id = ?1
             where a.id = ?1
             and c.deadline between ?2 and ?3
             and c.type = 'EXAM'
             and (ass.submitted is null or ass.submitted = false)
             and ass.grade is null
            """)
    <T> List<T> findTodoClassworkByUserIdAndBetween(String userId, Date startDate, Date endDate, Class<T> type);

    @Query("""
             SELECT c FROM Classwork c left join c.assignees a left join c.assignments ass on ass.author.id = ?1
             where a.id = ?1
             and c.classroom.id = ?4
             and c.deadline between ?2 and ?3
             and c.type = 'EXAM'
             and (ass.submitted is null or ass.submitted = false)
             and ass.grade is null
            """)
    <T> List<T> findTodoClassworkByUserIdAndBetweenAndClassroomId(String userId, Date startDate, Date endDate, String classroomId, Class<T> type);

    <T> List<T> findAllByAssignmentsAuthorIdAndAssignmentsSubmittedTrueOrAssignmentsAuthorIdAndAssignmentsGradeIsNotNullAndTypeIs(String userId, String userId1, ClassworkType cT, Class<T> type);
}
