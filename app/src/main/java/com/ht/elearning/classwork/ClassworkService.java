package com.ht.elearning.classwork;

import com.ht.elearning.classroom.ClassroomService;
import com.ht.elearning.classwork.dtos.CreateClassworkDto;
import com.ht.elearning.classwork.dtos.UpdateClassworkDto;
import com.ht.elearning.classwork.projections.StudentClassworkView;
import com.ht.elearning.config.HttpException;
import com.ht.elearning.file.FileService;
import com.ht.elearning.processor.ClassroomUpdateType;
import com.ht.elearning.processor.NotificationProcessor;
import com.ht.elearning.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassworkService {
    private final ClassworkRepository classworkRepository;
    private final ClassroomService classroomService;
    private final ClassworkCategoryService classworkCategoryService;
    private final NotificationProcessor notificationProcessor;
    private final FileService fileService;
    private final UserService userService;


    public List<?> findAllByClassroomId(String classroomId, String userId) {
        var isProvider = classroomService.isProvider(classroomId, userId);
        if (isProvider) {
            return classworkRepository.findAllByClassroomId(classroomId);
        }

        return classworkRepository.findAllByClassroomIdAndAssigneesId(classroomId, userId, StudentClassworkView.class);
    }


    public Classwork create(CreateClassworkDto createClassworkDto, String classroomId, String userId) {
        var classroom = classroomService.findById(classroomId);
        var isProvider = classroomService.isProvider(classroom, userId);
        if (!isProvider) throw new HttpException("You are not provider of this class", HttpStatus.FORBIDDEN);
        var category = createClassworkDto.getCategoryId() != null ? classworkCategoryService.findByIdAndClassroomId(createClassworkDto.getCategoryId(), classroomId) : null;
        var files = fileService.findAllById(createClassworkDto.getFileIds());

        var assignees = classroom.getUsers().stream()
                .filter(user -> createClassworkDto.getAssigneeIds().contains(user.getId()))
                .collect(Collectors.toSet());

        if (assignees.isEmpty()) throw new HttpException("Assignees must be specified", HttpStatus.BAD_REQUEST);

        if (createClassworkDto.getDeadline() != null && createClassworkDto.getDeadline().before(new Date()))
            throw new HttpException("Deadline must be in the future", HttpStatus.BAD_REQUEST);

        var author = userService.findById(userId);
        var classwork = Classwork.builder()
                .assignees(assignees)
                .files(new HashSet<>(files))
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

        notificationProcessor.classworkDidCreate(savedClasswork);
        notificationProcessor.classroomDidUpdate(classroom, ClassroomUpdateType.CLASSWORK);

        return savedClasswork;
    }


    public Classwork update(UpdateClassworkDto updateClassworkDto, String classworkId, String classroomId, String userId) {
        if (!classroomService.hasClasswork(classroomId, classworkId))
            throw new HttpException("Classwork not found", HttpStatus.BAD_REQUEST);

        var classroom = classroomService.findById(classroomId);
        var isProvider = classroomService.isProvider(classroom, userId);
        if (!isProvider) throw new HttpException("You are not provider of this class", HttpStatus.FORBIDDEN);

        var classwork = classworkRepository.findById(classworkId).orElseThrow(() -> new HttpException("Classwork not found", HttpStatus.BAD_REQUEST));
        AtomicBoolean isDeadlineChanged = new AtomicBoolean(false);
        Optional.ofNullable(updateClassworkDto.getTitle()).ifPresent(classwork::setTitle);
        Optional.ofNullable(updateClassworkDto.getDescription()).ifPresent(classwork::setDescription);
        Optional.of(updateClassworkDto.getScore()).ifPresent(classwork::setScore);

        //TODO: check if user want to remove deadline or not
        Optional.ofNullable(updateClassworkDto.getDeadline()).ifPresent(deadline -> {
            if (deadline.before(new Date()))
                throw new HttpException("Deadline must be in the future", HttpStatus.BAD_REQUEST);
            isDeadlineChanged.set(true);
            classwork.setDeadline(deadline);
        });

        Optional.ofNullable(updateClassworkDto.getCategoryId()).ifPresentOrElse(categoryId -> {
            var category = classworkCategoryService.findByIdAndClassroomId(updateClassworkDto.getCategoryId(), classroomId);
            classwork.setCategory(category);
        }, () -> {
            classwork.setCategory(null);
        });

        Optional.ofNullable(updateClassworkDto.getFileIds()).ifPresent(fileIds -> {
            var files = fileService.findAllById(fileIds);
            classwork.setFiles(new HashSet<>(files));
        });

        //TODO: handle notifications, assignment_schedules for changed assignees
        Optional.ofNullable(updateClassworkDto.getAssigneeIds()).ifPresent(assigneeIds -> {
            var assignees = classroom.getUsers().stream()
                    .filter(user -> updateClassworkDto.getAssigneeIds().contains(user.getId()))
                    .collect(Collectors.toSet());
            if (assignees.isEmpty()) throw new HttpException("Assignees must be specified", HttpStatus.BAD_REQUEST);
            classwork.setAssignees(assignees);
        });

        var savedClasswork = classworkRepository.save(classwork);

        if (isDeadlineChanged.get()) {
            notificationProcessor.classworkDeadlineDidUpdate(savedClasswork);
        }

        notificationProcessor.classroomDidUpdate(classroom, ClassroomUpdateType.CLASSWORK);

        return savedClasswork;
    }


    public boolean deleteById(String classworkId, String classroomId, String userId) {
        if (!classroomService.hasClasswork(classroomId, classworkId))
            throw new HttpException("Classwork not found", HttpStatus.BAD_REQUEST);

        var classroom = classroomService.findById(classroomId);
        var isProvider = classroomService.isProvider(classroom, userId);
        if (!isProvider) throw new HttpException("You are not provider of this class", HttpStatus.FORBIDDEN);
        var classwork = classworkRepository.findById(classworkId)
                .orElseThrow(() -> new HttpException("Classwork not found", HttpStatus.BAD_REQUEST));

        classworkRepository.delete(classwork);
        notificationProcessor.classroomDidUpdate(classroom, ClassroomUpdateType.CLASSWORK);
        return true;
    }


    public Classwork findById(String id) {
        return classworkRepository.findById(id).orElseThrow(() -> new HttpException("Classwork not found", HttpStatus.BAD_REQUEST));
    }


    public StudentClassworkView findByIdForStudent(String id) {
        return classworkRepository.findById(id, StudentClassworkView.class).orElseThrow(() -> new HttpException("Classwork not found", HttpStatus.BAD_REQUEST));
    }


    public Object findByClassroomIdAndClassworkId(String classroomId, String classworkId, String userId) {
        if (!classroomService.hasClasswork(classroomId, classworkId))
            throw new HttpException("Classwork not found", HttpStatus.BAD_REQUEST);

        var classroom = classroomService.find(classroomId, userId);

        var isProvider = classroomService.isProvider(classroom, userId);

        if (isProvider) return findById(classworkId);

        return findByIdForStudent(classworkId);
    }


    public boolean isAssignee(String classworkId, String userId) {
        return classworkRepository.existsByIdAndAssigneesId(classworkId, userId);
    }
}
