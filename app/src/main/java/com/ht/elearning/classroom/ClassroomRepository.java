package com.ht.elearning.classroom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClassroomRepository extends JpaRepository<Classroom, String> {
    boolean existsByInviteCode(String inviteCode);

    Optional<Classroom> findByInviteCode(String inviteCode);

    Optional<Classroom> findByIdAndOwnerId(String id, String ownerId);

    @Query(value = "select c.* from classroom_provider as cp join classrooms as c on c.id = cp.classroom_id where cp.provider_id = ?1", nativeQuery = true)
    List<Classroom> findAllTeachingClassrooms(String userId);

    @Query(value = "select c.* from classroom_user as cu join classrooms as c on c.id = cu.classroom_id where cu.user_id = ?1", nativeQuery = true)
    List<Classroom> findAllRegisteredClassrooms(String userId);

    List<Classroom> findAllByOwnerId(String ownerId);
}
