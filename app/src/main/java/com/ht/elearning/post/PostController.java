package com.ht.elearning.post;

import com.ht.elearning.config.Response;
import com.ht.elearning.post.dtos.CreatePostDto;
import com.ht.elearning.post.dtos.UpdatePostDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/posts")
@CrossOrigin
public class PostController {
    private final PostService postService;

    @GetMapping("{classroomId}")
    public ResponseEntity<Response<List<Post>>> findByClassroomId(@PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var posts = postService.findByClassroomId(classroomId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Ok",
                        true,
                        posts
                )
        );
    }

    @PostMapping
    public ResponseEntity<Response<Post>> create(@Valid @RequestBody CreatePostDto createPostDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var post = postService.create(createPostDto, authentication.getPrincipal().toString());
        return ResponseEntity.created(null).body(
                new Response<>(
                        HttpStatus.CREATED.value(),
                        "Created successfully",
                        true,
                        post
                )
        );
    }


    @PutMapping("{postId}")
    public ResponseEntity<Response<Post>> update(@Valid @RequestBody UpdatePostDto updatePostDto, @PathVariable String postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var post = postService.update(updatePostDto, postId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Updated successfully",
                        true,
                        post
                )
        );
    }


    @DeleteMapping("{postId}")
    public ResponseEntity<Response<?>> delete(@PathVariable String postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = postService.delete(postId, authentication.getPrincipal().toString());
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
