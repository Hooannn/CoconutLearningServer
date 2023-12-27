package com.ht.elearning.invitation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, String> {
    Optional<Invitation> findByEmailAndClassroomId(String email, String classroomId);
    boolean existsByEmailAndClassroomId(String email, String classroomId);
    void deleteByEmailAndClassroomId(String email, String classroomId);
}
