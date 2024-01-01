package com.ht.elearning.post;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, String> {
    Optional<Post> findByIdAndAuthorId(String id, String authorId);

    List<Post> findByClassroomId(String classId);
}
