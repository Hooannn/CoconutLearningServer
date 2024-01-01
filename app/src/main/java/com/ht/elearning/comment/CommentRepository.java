package com.ht.elearning.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, String> {
    Optional<Comment> findByIdAndAuthorId(String id, String authorId);
}
