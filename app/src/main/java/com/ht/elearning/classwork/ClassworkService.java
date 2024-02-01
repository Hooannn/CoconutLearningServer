package com.ht.elearning.classwork;

import com.ht.elearning.assignment.Assignment;
import com.ht.elearning.classroom.ClassroomService;
import com.ht.elearning.classwork.dtos.CreateClassworkDto;
import com.ht.elearning.classwork.dtos.UpdateClassworkDto;
import com.ht.elearning.classwork.projections.StudentClassworkView;
import com.ht.elearning.config.HttpException;
import com.ht.elearning.constants.ErrorMessage;
import com.ht.elearning.file.FileService;
import com.ht.elearning.processor.ClassroomUpdateType;
import com.ht.elearning.processor.NotificationProcessor;
import com.ht.elearning.user.User;
import com.ht.elearning.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public List<?> findAllExamByClassroomId(String classroomId, String userId) {
        var isProvider = classroomService.isProvider(classroomId, userId);
        if (isProvider) {
            return classworkRepository.findAllByClassroomIdAndTypeIs(classroomId, ClassworkType.EXAM);
        }

        return classworkRepository.findAllByClassroomIdAndAssigneesIdAndTypeIs(classroomId, userId, ClassworkType.EXAM, StudentClassworkView.class);
    }

    public Classwork create(CreateClassworkDto createClassworkDto, String classroomId, String userId) {
        var classroom = classroomService.findById(classroomId);
        var isProvider = classroomService.isProvider(classroom, userId);
        if (!isProvider) throw new HttpException(ErrorMessage.USER_IS_NOT_PROVIDER, HttpStatus.FORBIDDEN);
        var category = createClassworkDto.getCategoryId() != null ? classworkCategoryService.findByIdAndClassroomId(createClassworkDto.getCategoryId(), classroomId) : null;
        var files = fileService.findAllById(createClassworkDto.getFileIds());

        var assignees = classroom.getUsers().stream()
                .filter(user -> createClassworkDto.getAssigneeIds().contains(user.getId()))
                .collect(Collectors.toSet());

        if (assignees.isEmpty())
            throw new HttpException(ErrorMessage.ASSIGNEES_MUST_BE_SPECIFIED, HttpStatus.BAD_REQUEST);

        if (createClassworkDto.getDeadline() != null && createClassworkDto.getDeadline().before(new Date()))
            throw new HttpException(ErrorMessage.DEADLINE_MUST_BE_IN_FUTURE, HttpStatus.BAD_REQUEST);

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
            throw new HttpException(ErrorMessage.CLASSWORK_NOT_FOUND, HttpStatus.BAD_REQUEST);

        var classroom = classroomService.findById(classroomId);
        var isProvider = classroomService.isProvider(classroom, userId);
        if (!isProvider) throw new HttpException(ErrorMessage.USER_IS_NOT_PROVIDER, HttpStatus.FORBIDDEN);

        var classwork = classworkRepository.findById(classworkId)
                .orElseThrow(() -> new HttpException(ErrorMessage.CLASSWORK_NOT_FOUND, HttpStatus.BAD_REQUEST));

        AtomicBoolean isDeadlineChanged = new AtomicBoolean(false);

        Optional.ofNullable(updateClassworkDto.getTitle()).ifPresent(classwork::setTitle);
        Optional.ofNullable(updateClassworkDto.getDescription()).ifPresent(classwork::setDescription);
        Optional.of(updateClassworkDto.getScore()).ifPresent(classwork::setScore);

        Optional.ofNullable(updateClassworkDto.getDeadline()).ifPresentOrElse(deadline -> {
            if (deadline.before(new Date()))
                throw new HttpException(ErrorMessage.DEADLINE_MUST_BE_IN_FUTURE, HttpStatus.BAD_REQUEST);
            if (classwork.getDeadline() == null || classwork.getDeadline().compareTo(updateClassworkDto.getDeadline()) != 0) {
                isDeadlineChanged.set(true);
                classwork.setDeadline(deadline);
            }
        }, () -> {
            if (classwork.getDeadline() == null) return;
            isDeadlineChanged.set(true);
            classwork.setDeadline(null);
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

        AtomicReference<Set<User>> newAssignees = new AtomicReference<>(new HashSet<>());

        Optional.ofNullable(updateClassworkDto.getAssigneeIds()).ifPresent(assigneeIds -> {
            var assignees = classroom.getUsers().stream()
                    .filter(user -> updateClassworkDto.getAssigneeIds().contains(user.getId()))
                    .collect(Collectors.toSet());

            if (assignees.isEmpty())
                throw new HttpException(ErrorMessage.ASSIGNEES_MUST_BE_SPECIFIED, HttpStatus.BAD_REQUEST);

            newAssignees.set(assignees.stream()
                    .filter(assignee -> !classwork.getAssignees().contains(assignee))
                    .collect(Collectors.toSet()));

            classwork.setAssignees(assignees);
        });

        var savedClasswork = classworkRepository.save(classwork);

        if (isDeadlineChanged.get()) {
            notificationProcessor.classworkDeadlineDidUpdate(savedClasswork);
        }

        if (!newAssignees.get().isEmpty()) {
            notificationProcessor.classworkAssigneesDidUpdate(savedClasswork, newAssignees.get());
            if (!isDeadlineChanged.get()) {
                notificationProcessor.classworkDeadlineDidUpdate(savedClasswork);
            }
        }

        notificationProcessor.classroomDidUpdate(classroom, ClassroomUpdateType.CLASSWORK);

        return savedClasswork;
    }

    public boolean deleteById(String classworkId, String classroomId, String userId) {
        if (!classroomService.hasClasswork(classroomId, classworkId))
            throw new HttpException(ErrorMessage.CLASSWORK_NOT_FOUND, HttpStatus.BAD_REQUEST);

        var classroom = classroomService.findById(classroomId);
        var isProvider = classroomService.isProvider(classroom, userId);
        if (!isProvider) throw new HttpException(ErrorMessage.USER_IS_NOT_PROVIDER, HttpStatus.FORBIDDEN);
        var classwork = classworkRepository.findById(classworkId)
                .orElseThrow(() -> new HttpException(ErrorMessage.CLASSWORK_NOT_FOUND, HttpStatus.BAD_REQUEST));

        classworkRepository.delete(classwork);
        notificationProcessor.classroomDidUpdate(classroom, ClassroomUpdateType.CLASSWORK);
        return true;
    }

    public Classwork findById(String id) {
        return classworkRepository.findById(id)
                .orElseThrow(() -> new HttpException(ErrorMessage.CLASSWORK_NOT_FOUND, HttpStatus.BAD_REQUEST));
    }

    public StudentClassworkView findByIdForStudent(String id) {
        return classworkRepository.findById(id, StudentClassworkView.class)
                .orElseThrow(() -> new HttpException(ErrorMessage.CLASSWORK_NOT_FOUND, HttpStatus.BAD_REQUEST));
    }

    public Object findByClassroomIdAndClassworkId(String classroomId, String classworkId, String userId) {
        if (!classroomService.hasClasswork(classroomId, classworkId))
            throw new HttpException(ErrorMessage.CLASSWORK_NOT_FOUND, HttpStatus.BAD_REQUEST);

        var classroom = classroomService.find(classroomId, userId);

        var isProvider = classroomService.isProvider(classroom, userId);

        if (isProvider) return findById(classworkId);

        return findByIdForStudent(classworkId);
    }

    public boolean isAssignee(String classworkId, String userId) {
        return classworkRepository.existsByIdAndAssigneesId(classworkId, userId);
    }

    public List<?> findUpcomingByClassroomId(String classroomId, String userId, boolean forProvider) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlus7 = now.plusDays(7);
        Date nowPlus7Date = Date.from(nowPlus7.atZone(ZoneId.systemDefault()).toInstant());
        if (forProvider) {
            boolean isProvider = classroomService.isProvider(classroomId, userId);
            if (!isProvider) throw new HttpException(ErrorMessage.USER_IS_NOT_PROVIDER, HttpStatus.FORBIDDEN);
            return classworkRepository.findAllByClassroomIdAndDeadlineBetweenOrderByDeadlineAsc(classroomId, new Date(), nowPlus7Date);
        } else {
            return classworkRepository.findAllByClassroomIdAndAssigneesIdAndDeadlineBetweenOrderByDeadlineAsc(classroomId, userId, new Date(), nowPlus7Date, StudentClassworkView.class);
        }
    }

    public List<StudentClassworkView> findTodo(String userId) {
        return classworkRepository.findTodoClassworkByUserId(userId, StudentClassworkView.class);
    }

    public List<StudentClassworkView> findDone(String userId) {
        return classworkRepository.findAllByAssignmentsAuthorIdAndAssignmentsSubmittedTrueOrAssignmentsAuthorIdAndAssignmentsGradeIsNotNullAndTypeIs(
                userId,
                userId,
                ClassworkType.EXAM,
                StudentClassworkView.class);
    }

    public List<Classwork> findNeedReview(String userId) {
        return classworkRepository.findAllByClassroomProvidersIdOrClassroomOwnerIdAndTypeIsOrderByDeadlineAsc(userId, userId, ClassworkType.EXAM);
    }

    public List<?> findCalendar(Date startDate, Date endDate, String userId) {
        List<StudentClassworkView> todo = classworkRepository.findTodoClassworkByUserIdAndBetween(
                userId, startDate, endDate, StudentClassworkView.class);
        List<Classwork> needReview = classworkRepository.findAllByClassroomProvidersIdOrClassroomOwnerIdAndDeadlineIsBetweenAndTypeIsOrderByDeadlineAsc(userId, userId, startDate, endDate, ClassworkType.EXAM);
        return Stream.concat(todo.stream(), needReview.stream())
                .toList();
    }

    public List<?> findCalendarByClassroomId(String classroomId, Date startDate, Date endDate, String userId) {
        var isMember = classroomService.isMember(classroomId, userId);
        if (!isMember) throw new HttpException(ErrorMessage.USER_IS_NOT_MEMBER, HttpStatus.FORBIDDEN);
        List<StudentClassworkView> todo = classworkRepository.findTodoClassworkByUserIdAndBetweenAndClassroomId(
                userId, startDate, endDate, classroomId, StudentClassworkView.class);
        List<Classwork> needReview = classworkRepository.findAllByClassroomProvidersIdOrClassroomOwnerIdAndDeadlineIsBetweenAndTypeIsAndClassroomIdOrderByDeadlineAsc(userId, userId, startDate, endDate, ClassworkType.EXAM, classroomId);
        return Stream.concat(todo.stream(), needReview.stream())
                .toList();
    }
}
