package com.ht.elearning.classroom;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClassroomRepository extends JpaRepository<Classroom, String> {
    boolean existsByInviteCode(String inviteCode);

    Optional<Classroom> findByInviteCode(String inviteCode);

    Optional<Classroom> findByIdAndOwnerId(String id, String ownerId);
}
