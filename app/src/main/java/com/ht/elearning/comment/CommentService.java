package com.ht.elearning.comment;

import com.ht.elearning.classroom.ClassroomService;
import com.ht.elearning.comment.dtos.CreateCommentDto;
import com.ht.elearning.comment.dtos.UpdateCommentDto;
import com.ht.elearning.config.HttpException;
import com.ht.elearning.post.PostService;
import com.ht.elearning.processor.ClassroomUpdateType;
import com.ht.elearning.processor.NotificationProcessor;
import com.ht.elearning.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final ClassroomService classroomService;
    private final UserService userService;
    private final NotificationProcessor notificationProcessor;
    private final PostService postService;

    public Comment create(CreateCommentDto createCommentDto, String authorId) {
        var classroom = classroomService.findById(createCommentDto.getClassroomId());
        var isMember = classroomService.isMember(classroom, authorId);
        if (!isMember) throw new HttpException("You are not member of this class", HttpStatus.FORBIDDEN);

        var post = postService.findById(createCommentDto.getPostId());
        if (!post.getClassroom().getId().equals(createCommentDto.getClassroomId()))
            throw new HttpException("No permission", HttpStatus.FORBIDDEN);

        var author = userService.findById(authorId);
        var comment = Comment.builder()
                .body(createCommentDto.getBody())
                .author(author)
                .post(post)
                .build();

        var savedComment = commentRepository.save(comment);

        notificationProcessor.processNewComment(savedComment);
        notificationProcessor.classroomDidUpdate(classroom, ClassroomUpdateType.COMMENT);
        return savedComment;
    }


    public Comment update(UpdateCommentDto updateCommentDto, String commentId, String authorId) {
        var classroom = classroomService.findById(updateCommentDto.getClassroomId());
        var comment = commentRepository.findByIdAndAuthorId(commentId, authorId)
                .orElseThrow(() -> new HttpException("Comment not found", HttpStatus.BAD_REQUEST));

        Optional.ofNullable(updateCommentDto.getBody()).ifPresent(comment::setBody);

        notificationProcessor.classroomDidUpdate(classroom, ClassroomUpdateType.COMMENT);
        return commentRepository.save(comment);
    }


    public boolean delete(String id, String classroomId, String authorId) {
        var classroom = classroomService.findById(classroomId);
        var comment = commentRepository.findByIdAndAuthorId(id, authorId)
                .orElseThrow(() -> new HttpException("Comment not found", HttpStatus.BAD_REQUEST));
        commentRepository.delete(comment);
        notificationProcessor.classroomDidUpdate(classroom, ClassroomUpdateType.COMMENT);
        return true;
    }
}
