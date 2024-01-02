package com.ht.elearning.classwork;

import com.ht.elearning.classroom.ClassroomService;
import com.ht.elearning.classwork.dtos.CreateClassworkDto;
import com.ht.elearning.classwork.dtos.UpdateClassworkDto;
import com.ht.elearning.config.HttpException;
import com.ht.elearning.file.FileRepository;
import com.ht.elearning.processor.NotificationProcessor;
import com.ht.elearning.user.UserRepository;
import com.ht.elearning.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClassworkService {
    private final ClassworkRepository classworkRepository;
    private final ClassroomService classroomService;
    private final ClassworkCategoryService classworkCategoryService;
    private final NotificationProcessor notificationProcessor;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final UserService userService;


    public List<Classwork> findAllByClassroomId(String classroomId, String userId) {
        var isMember = classroomService.isMember(classroomId, userId);
        if (!isMember) throw new HttpException("You are not member of this class", HttpStatus.FORBIDDEN);

        return classworkRepository.findAllByClassroomId(classroomId);
    }


    public Classwork create(CreateClassworkDto createClassworkDto, String classroomId, String userId) {
        var classroom = classroomService.findById(classroomId);
        var isMember = classroomService.isMember(classroom, userId);
        if (!isMember) throw new HttpException("You are not member of this class", HttpStatus.FORBIDDEN);
        var category = classworkCategoryService.findById(createClassworkDto.getCategoryId());
        var files = fileRepository.findAllById(createClassworkDto.getFileIds());
        var assignees = userRepository.findAllById(createClassworkDto.getAssigneeIds());
        if (assignees.isEmpty()) throw new HttpException("Assignees must be specified", HttpStatus.BAD_REQUEST);
        var author = userService.findById(userId);
        var classwork = Classwork.builder()
                .assignees(assignees)
                .files(files)
                .score(createClassworkDto.getScore())
                .title(createClassworkDto.getTitle())
                .description(createClassworkDto.getDescription())
                .deadline(createClassworkDto.getDeadline())
                .classroom(classroom)
                .type(createClassworkDto.getType())
                .category(category)
                .author(author)
                .build();

        var savedClasswork = classworkRepository.save(classwork);

        notificationProcessor.processNewClasswork(savedClasswork);

        return savedClasswork;
    }


    public Classwork update(UpdateClassworkDto updateClassworkDto, String classworkId, String classroomId, String userId) {
        var isMember = classroomService.isMember(classroomId, userId);
        if (!isMember) throw new HttpException("You are not member of this class", HttpStatus.FORBIDDEN);
        var classwork = classworkRepository.findById(classworkId).orElseThrow(() -> new HttpException("Classwork not found", HttpStatus.BAD_REQUEST));

        Optional.ofNullable(updateClassworkDto.getTitle()).ifPresent(classwork::setTitle);
        Optional.ofNullable(updateClassworkDto.getDescription()).ifPresent(classwork::setDescription);
        Optional.of(updateClassworkDto.getScore()).ifPresent(classwork::setScore);
        Optional.ofNullable(updateClassworkDto.getDeadline()).ifPresent(classwork::setDeadline);
        Optional.ofNullable(updateClassworkDto.getCategoryId()).ifPresent(categoryId -> {
            var category = classworkCategoryService.findById(updateClassworkDto.getCategoryId());
            classwork.setCategory(category);
        });

        Optional.ofNullable(updateClassworkDto.getFileIds()).ifPresent(fileIds -> {
            var files = fileRepository.findAllById(fileIds);
            classwork.setFiles(files);
        });

        Optional.ofNullable(updateClassworkDto.getAssigneeIds()).ifPresent(assigneeIds -> {
            var assignees = userRepository.findAllById(updateClassworkDto.getAssigneeIds());
            if (assignees.isEmpty()) throw new HttpException("Assignees must be specified", HttpStatus.BAD_REQUEST);
            classwork.setAssignees(assignees);
        });
        var savedClasswork = classworkRepository.save(classwork);

        // handle notification

        return savedClasswork;
    }


    public boolean deleteById(String classroomId, String classworkId, String userId) {
        var isMember = classroomService.isMember(classroomId, userId);
        if (!isMember) throw new HttpException("You are not member of this class", HttpStatus.FORBIDDEN);
        var classwork = classworkRepository.findById(classworkId)
                .orElseThrow(() -> new HttpException("Classwork not found", HttpStatus.BAD_REQUEST));

        classworkRepository.delete(classwork);
        return true;
    }
}
