package com.ht.elearning.file;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, String> {
    Optional<File> findByIdAndCreatorId(String id, String createdBy);

    List<File> findAllByCreatorId(String userId);
}
