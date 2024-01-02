package com.ht.elearning.classwork;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassworkCategoryRepository extends JpaRepository<ClassworkCategory, String> {
    List<ClassworkCategory> findAllByClassroomId(String classroomId);
}
