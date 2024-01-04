package com.ht.elearning.classwork;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassworkCategoryRepository extends JpaRepository<ClassworkCategory, String> {
    List<ClassworkCategory> findAllByClassroomId(String classroomId);

    Optional<ClassworkCategory> findByIdAndClassroomId(String categoryId, String classroomId);
}
