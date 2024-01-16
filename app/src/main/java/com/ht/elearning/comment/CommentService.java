package com.ht.elearning.comment;

import com.ht.elearning.classroom.ClassroomService;
import com.ht.elearning.classwork.ClassworkService;
import com.ht.elearning.comment.dtos.CreateClassworkCommentDto;
import com.ht.elearning.comment.dtos.CreatePostCommentDto;
import com.ht.elearning.comment.dtos.UpdateCommentDto;
import com.ht.elearning.config.HttpException;
import com.ht.elearning.post.PostService;
import com.ht.elearning.processor.ClassroomUpdateType;
import com.ht.elearning.processor.NotificationProcessor;
import com.ht.elearning.user.UserService;
import jakarta.transaction.Transactional;
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
    private final ClassworkService classworkService;


    public Comment createForPost(CreatePostCommentDto createPostCommentDto, String authorId) {
        var classroom = classroomService.findById(createPostCommentDto.getClassroomId());
        var isMember = classroomService.isMember(classroom, authorId);
        if (!isMember) throw new HttpException("You are not member of this class", HttpStatus.FORBIDDEN);

        var post = postService.findById(createPostCommentDto.getPostId());
        if (!post.getClassroom().getId().equals(createPostCommentDto.getClassroomId()))
            throw new HttpException("No permission", HttpStatus.FORBIDDEN);

        var author = userService.findById(authorId);
        var comment = Comment.builder()
                .body(createPostCommentDto.getBody())
                .author(author)
                .post(post)
                .build();

        var savedComment = commentRepository.save(comment);

        notificationProcessor.commentDidCreate(savedComment);
        notificationProcessor.classroomDidUpdate(classroom, ClassroomUpdateType.COMMENT);
        return savedComment;
    }


    public Comment createForClasswork(CreateClassworkCommentDto createClassworkCommentDto, String authorId) {
        var classroom = classroomService.findById(createClassworkCommentDto.getClassroomId());
        var isMember = classroomService.isMember(classroom, authorId);
        if (!isMember) throw new HttpException("You are not member of this class", HttpStatus.FORBIDDEN);

        var classwork = classworkService.findById(createClassworkCommentDto.getClassworkId());
        if (!classwork.getClassroom().getId().equals(createClassworkCommentDto.getClassroomId()))
            throw new HttpException("No permission", HttpStatus.FORBIDDEN);

        var author = userService.findById(authorId);
        var comment = Comment.builder()
                .body(createClassworkCommentDto.getBody())
                .author(author)
                .classwork(classwork)
                .build();

        var savedComment = commentRepository.save(comment);

        notificationProcessor.commentDidCreate(savedComment);
        notificationProcessor.classroomDidUpdate(classroom, ClassroomUpdateType.COMMENT);
        return savedComment;
    }


    public Comment update(UpdateCommentDto updateCommentDto, String commentId, String authorId) {
        var classroom = classroomService.findById(updateCommentDto.getClassroomId());
        var comment = commentRepository.findByIdAndAuthorId(commentId, authorId)
                .orElseThrow(() -> new HttpException("Comment not found", HttpStatus.BAD_REQUEST));

        Optional.ofNullable(updateCommentDto.getBody()).ifPresent(comment::setBody);

        var savedComment = commentRepository.save(comment);
        notificationProcessor.classroomDidUpdate(classroom, ClassroomUpdateType.COMMENT);
        return savedComment;
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
