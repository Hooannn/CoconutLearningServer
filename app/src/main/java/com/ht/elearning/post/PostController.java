package com.ht.elearning.post;

import com.ht.elearning.config.Response;
import com.ht.elearning.constants.ResponseMessage;
import com.ht.elearning.post.dtos.CreatePostDto;
import com.ht.elearning.post.dtos.UpdatePostDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/posts")
@CrossOrigin
public class PostController {
    private final PostService postService;

    @Operation(summary = "Find all posts in a classroom")
    @GetMapping("{classroomId}")
    public ResponseEntity<Response<List<Post>>> findByClassroomId(@PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var posts = postService.findByClassroomId(classroomId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.OK,
                        true,
                        posts
                )
        );
    }

    @Operation(summary = "Create a post in a classroom")
    @PostMapping
    public ResponseEntity<Response<Post>> create(@Valid @RequestBody CreatePostDto createPostDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var post = postService.create(createPostDto, authentication.getPrincipal().toString());
        return ResponseEntity.created(null).body(
                new Response<>(
                        HttpStatus.CREATED.value(),
                        ResponseMessage.CREATED,
                        true,
                        post
                )
        );
    }

    @Operation(summary = "Update a post by its id")
    @PutMapping("{postId}")
    public ResponseEntity<Response<Post>> update(@Valid @RequestBody UpdatePostDto updatePostDto, @PathVariable String postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var post = postService.update(updatePostDto, postId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.UPDATED,
                        true,
                        post
                )
        );
    }

    @Operation(summary = "Delete a post by its id")
    @DeleteMapping("/{classroomId}/{postId}")
    public ResponseEntity<Response<?>> delete(@PathVariable String postId, @PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = postService.delete(postId, classroomId, authentication.getPrincipal().toString());
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
