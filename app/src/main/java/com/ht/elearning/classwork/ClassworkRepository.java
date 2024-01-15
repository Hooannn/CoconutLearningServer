package com.ht.elearning.classwork;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassworkRepository extends JpaRepository<Classwork, String> {
    List<Classwork> findAllByClassroomId(String classroomId);

    boolean existsByIdAndClassroomId(String classworkId, String classroomId);

    boolean existsByIdAndAssigneesId(String classworkId, String userId);

    List<Classwork> findAllByClassroomIdAndAssigneesId(String classroomId, String userId);
}
