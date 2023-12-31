package com.ht.elearning.file;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File, String> {
    Optional<File> findByIdAndCreatorId(String id, String createdBy);
}
