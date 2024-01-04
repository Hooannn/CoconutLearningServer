package com.ht.elearning.comment;

import com.ht.elearning.comment.dtos.CreateCommentDto;
import com.ht.elearning.comment.dtos.UpdateCommentDto;
import com.ht.elearning.config.Response;
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


    @PostMapping
    public ResponseEntity<Response<Comment>> create(@Valid @RequestBody CreateCommentDto createCommentDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var comment = commentService.create(createCommentDto, authentication.getPrincipal().toString());
        return ResponseEntity.created(null).body(
                new Response<>(
                        HttpStatus.CREATED.value(),
                        "Created successfully",
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
                        "Updated successfully",
                        true,
                        comment
                )
        );
    }


    @DeleteMapping("{commentId}")
    public ResponseEntity<Response<?>> delete(@PathVariable String commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = commentService.delete(commentId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Deleted",
                        success,
                        null
                )
        );
    }
}
