package com.ht.elearning.classwork;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassworkRepository extends JpaRepository<Classwork, String> {
    List<Classwork> findAllByClassroomId(String classroomId);
}
