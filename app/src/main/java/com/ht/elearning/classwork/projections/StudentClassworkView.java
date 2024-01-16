package com.ht.elearning.classwork.projections;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ht.elearning.classwork.ClassworkCategory;
import com.ht.elearning.classwork.ClassworkType;
import com.ht.elearning.comment.Comment;
import com.ht.elearning.file.File;
import com.ht.elearning.user.User;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface StudentClassworkView {
    String getId();

    String getTitle();

    String getDescription();

    ClassworkType getType();

    int getScore();

    Date getDeadline();

    List<Comment> getComments();

    Set<File> getFiles();

    ClassworkCategory getCategory();

    User getAuthor();

    @JsonProperty("created_at")
    Date getCreatedAt();

    @JsonProperty("updated_at")
    Date getUpdatedAt();
}
