package com.ht.elearning.assignment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, String> {
    Optional<Grade> findByIdAndGradedById(String id, String gradedById);
}
