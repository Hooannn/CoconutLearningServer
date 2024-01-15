package com.ht.elearning.assignment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssignmentRepository extends JpaRepository<Assignment, String> {
    Optional<Assignment> findByClassworkIdAndAuthorId(String classworkId, String userId);

    boolean existsByClassworkIdAndAuthorId(String classworkId, String userId);

    long countByClassworkIdAndSubmittedFalse(String classworkId);

    List<Assignment> findAllByClassworkId(String classworkId);
}
