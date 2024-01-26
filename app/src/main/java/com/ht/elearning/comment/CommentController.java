package com.ht.elearning.comment;

import com.ht.elearning.comment.dtos.CreateClassworkCommentDto;
import com.ht.elearning.comment.dtos.CreatePostCommentDto;
import com.ht.elearning.comment.dtos.UpdateCommentDto;
import com.ht.elearning.config.Response;
import com.ht.elearning.constants.ResponseMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/comments")
@CrossOrigin
public class CommentController {
    private final CommentService commentService;


    @PostMapping("/post")
    public ResponseEntity<Response<Comment>> create(@Valid @RequestBody CreatePostCommentDto createPostCommentDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var comment = commentService.createForPost(createPostCommentDto, authentication.getPrincipal().toString());
        return ResponseEntity.created(null).body(
                new Response<>(
                        HttpStatus.CREATED.value(),
                        ResponseMessage.CREATED,
                        true,
                        comment
                )
        );
    }


    @PostMapping("/classwork")
    public ResponseEntity<Response<Comment>> create(@Valid @RequestBody CreateClassworkCommentDto createClassworkCommentDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var comment = commentService.createForClasswork(createClassworkCommentDto, authentication.getPrincipal().toString());
        return ResponseEntity.created(null).body(
                new Response<>(
                        HttpStatus.CREATED.value(),
                        ResponseMessage.CREATED,
                        true,
                        comment
                )
        );
    }


    @PutMapping("{commentId}")
    public ResponseEntity<Response<Comment>> update(@Valid @RequestBody UpdateCommentDto updateCommentDto, @PathVariable String commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var comment = commentService.update(updateCommentDto, commentId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.UPDATED,
                        true,
                        comment
                )
        );
    }


    @DeleteMapping("/{classroomId}/{commentId}")
    public ResponseEntity<Response<?>> delete(@PathVariable String commentId, @PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = commentService.delete(commentId, classroomId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.DELETED,
                        success,
                        null
                )
        );
    }
}
