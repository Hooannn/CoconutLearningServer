package com.ht.elearning.post;

import com.ht.elearning.classroom.ClassroomService;
import com.ht.elearning.config.HttpException;
import com.ht.elearning.file.FileRepository;
import com.ht.elearning.file.FileService;
import com.ht.elearning.post.dtos.CreatePostDto;
import com.ht.elearning.post.dtos.UpdatePostDto;
import com.ht.elearning.processor.ClassroomUpdateType;
import com.ht.elearning.processor.NotificationProcessor;
import com.ht.elearning.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final UserService userService;
    private final ClassroomService classroomService;
    private final FileService fileService;
    private final PostRepository postRepository;
    private final NotificationProcessor notificationProcessor;


    public Post findById(String id) {
        return postRepository.findById(id).orElseThrow(() -> new HttpException("Post not found", HttpStatus.BAD_REQUEST));
    }


    public Post create(CreatePostDto createPostDto, String authorId) {
        var classroom = classroomService.findById(createPostDto.getClassroomId());
        var isMember = classroomService.isMember(classroom, authorId);
        if (!isMember) throw new HttpException("You are not member of this class", HttpStatus.FORBIDDEN);

        var author = userService.findById(authorId);
        var files = fileService.findAllById(createPostDto.getFileIds());
        var post = Post.builder()
                .author(author)
                .body(createPostDto.getBody())
                .classroom(classroom)
                .files(new HashSet<>(files))
                .build();

        var savedPost = postRepository.save(post);

        notificationProcessor.postDidCreate(savedPost);
        notificationProcessor.classroomDidUpdate(classroom, ClassroomUpdateType.POST);
        return savedPost;
    }


    public Post update(UpdatePostDto updatePostDto, String postId, String authorId) {
        var classroom = classroomService.findById(updatePostDto.getClassroomId());
        var post = postRepository.findByIdAndAuthorId(postId, authorId).orElseThrow(() -> new HttpException("Post not found", HttpStatus.BAD_REQUEST));
        Optional.ofNullable(updatePostDto.getBody()).ifPresent(post::setBody);
        Optional.ofNullable(updatePostDto.getFileIds()).ifPresent(fileIds -> {
            var files = fileService.findAllById(fileIds);
            post.setFiles(new HashSet<>(files));
        });
        var savedPost = postRepository.save(post);
        notificationProcessor.classroomDidUpdate(classroom, ClassroomUpdateType.POST);
        return savedPost;
    }


    public boolean delete(String postId, String classroomId, String authorId) {
        var classroom = classroomService.findById(classroomId);
        var post = postRepository.findByIdAndAuthorId(postId, authorId).orElseThrow(() -> new HttpException("Post not found", HttpStatus.BAD_REQUEST));
        postRepository.delete(post);
        notificationProcessor.classroomDidUpdate(classroom, ClassroomUpdateType.POST);
        return true;
    }


    public List<Post> findByClassroomId(String classId, String userId) {
        var isMember = classroomService.isMember(classId, userId);
        if (!isMember) throw new HttpException("You are not member of this class", HttpStatus.FORBIDDEN);

        return postRepository.findByClassroomId(classId);
    }
}
