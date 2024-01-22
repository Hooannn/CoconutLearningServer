package com.ht.elearning.assignment;

import com.ht.elearning.assignment.dtos.CreateAssignmentDto;
import com.ht.elearning.assignment.dtos.CreateGradeDto;
import com.ht.elearning.assignment.dtos.UpdateAssignmentDto;
import com.ht.elearning.assignment.dtos.UpdateGradeDto;
import com.ht.elearning.classroom.ClassroomService;
import com.ht.elearning.classwork.ClassworkService;
import com.ht.elearning.config.HttpException;
import com.ht.elearning.file.File;
import com.ht.elearning.file.FileService;
import com.ht.elearning.processor.ClassroomUpdateType;
import com.ht.elearning.processor.NotificationProcessor;
import com.ht.elearning.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final GradeRepository gradeRepository;
    private final ClassroomService classroomService;
    private final NotificationProcessor notificationProcessor;
    private final ClassworkService classworkService;
    private final UserService userService;
    private final FileService fileService;

    public Assignment findByClassworkIdAndAuthorId(String classworkId, String authorId) {
        return assignmentRepository.findByClassworkIdAndAuthorId(classworkId, authorId)
                .orElseThrow(() -> new HttpException("Assignment not found", HttpStatus.BAD_REQUEST));
    }

    public Assignment create(CreateAssignmentDto createAssignmentDto, String userId) {
        if (!classroomService.hasClasswork(createAssignmentDto.getClassroomId(), createAssignmentDto.getClassworkId()))
            throw new HttpException("Classwork not found", HttpStatus.BAD_REQUEST);

        if (!classworkService.isAssignee(createAssignmentDto.getClassworkId(), userId))
            throw new HttpException("No permission", HttpStatus.FORBIDDEN);

        var classwork = classworkService.findById(createAssignmentDto.getClassworkId());

        if (classwork.getDeadline().before(new Date()))
            throw new HttpException("Deadline is passed", HttpStatus.BAD_REQUEST);

        var assignment = assignmentRepository.findByClassworkIdAndAuthorId(createAssignmentDto.getClassworkId(), userId).orElse(null);
        Set<File> files = new HashSet<>(fileService.findAllById(createAssignmentDto.getFileIds()));
        boolean isNew = true;
        if (assignment == null) {
            assignment = Assignment.builder()
                    .author(userService.findById(userId))
                    .classwork(classwork)
                    .description(createAssignmentDto.getDescription())
                    .submitted(createAssignmentDto.isSubmitted())
                    .files(files)
                    .build();
        } else {
            assignment.setSubmitted(createAssignmentDto.isSubmitted());
            assignment.setDescription(createAssignmentDto.getDescription());
            assignment.setFiles(files);
            isNew = false;
        }

        var savedAssignment = assignmentRepository.save(assignment);

        if (isNew) notificationProcessor.assignmentDidCreate(savedAssignment);

        return savedAssignment;
    }

    public Assignment update(UpdateAssignmentDto updateAssignmentDto, String classworkId, String classroomId, String userId) {
        var classroom = classroomService.findById(classroomId);
        var assignment = findByClassworkIdAndAuthorId(classworkId, userId);

        if (assignment.getClasswork().getDeadline().before(new Date()))
            throw new HttpException("Deadline is passed", HttpStatus.BAD_REQUEST);

        Optional.ofNullable(updateAssignmentDto.getDescription()).ifPresent(assignment::setDescription);
        Optional.ofNullable(updateAssignmentDto.getFileIds()).ifPresent(fileIds -> {
            var files = fileService.findAllById(fileIds);
            assignment.setFiles(new HashSet<>(files));
        });

        var savedAssignment = assignmentRepository.save(assignment);
        notificationProcessor.classroomDidUpdate(classroom, ClassroomUpdateType.ASSIGNMENT);
        return savedAssignment;
    }

    public Assignment updateSubmittedStatus(boolean isSubmitted, String classworkId, String classroomId, String userId) {
        var classroom = classroomService.findById(classroomId);
        var assignment = assignmentRepository.findByClassworkIdAndAuthorId(classworkId, userId)
                .orElseThrow(() -> new HttpException("Assignment not found", HttpStatus.BAD_REQUEST));
        assignment.setSubmitted(isSubmitted);
        var savedAssignment = assignmentRepository.save(assignment);

        notificationProcessor.classroomDidUpdate(classroom, ClassroomUpdateType.ASSIGNMENT);
        return savedAssignment;
    }

    public Assignment findByClassworkIdAndAuthorId(String classworkId, String classroomId, String authorId, String authenticatedUserId) {
        var isProvider = classroomService.isProvider(classroomId, authenticatedUserId);
        if (isProvider) {
            var assignment = findByClassworkIdAndAuthorId(classworkId, authorId);

            if (!classroomService.hasClasswork(classroomId, assignment.getClasswork().getId()))
                throw new HttpException("Assignment not found", HttpStatus.BAD_REQUEST);

            return assignment;
        }
        return findByClassworkIdAndAuthorId(classworkId, authenticatedUserId);
    }

    public boolean deleteByClassworkIdAndAuthorId(String classworkId, String classroomId, String userId) {
        var classroom = classroomService.findById(classroomId);
        var assignment = assignmentRepository.findByClassworkIdAndAuthorId(classworkId, userId)
                .orElseThrow(() -> new HttpException("Assignment not found", HttpStatus.BAD_REQUEST));

        assignmentRepository.delete(assignment);
        notificationProcessor.classroomDidUpdate(classroom, ClassroomUpdateType.ASSIGNMENT);
        return true;
    }

    public long countSubmitted(String classworkId, String classroomId, String userId) {
        var isProvider = classroomService.isProvider(classroomId, userId);

        if (!isProvider) throw new HttpException("No permission", HttpStatus.FORBIDDEN);

        if (!classroomService.hasClasswork(classroomId, classworkId))
            throw new HttpException("Classwork not found", HttpStatus.BAD_REQUEST);

        return assignmentRepository.countByClassworkIdAndSubmittedFalse(classworkId);
    }

    public List<Assignment> findAllByClassworkId(String classworkId, String classroomId, String userId) {
        var isProvider = classroomService.isProvider(classroomId, userId);

        if (!isProvider) throw new HttpException("No permission", HttpStatus.FORBIDDEN);

        if (!classroomService.hasClasswork(classroomId, classworkId))
            throw new HttpException("Classwork not found", HttpStatus.BAD_REQUEST);

        return assignmentRepository.findAllByClassworkId(classworkId);
    }

    @Transactional
    public Grade createGrade(CreateGradeDto createGradeDto, String classworkId, String classroomId, String studentId, String gradedBy) {
        var isProvider = classroomService.isProvider(classroomId, gradedBy);

        if (!isProvider) throw new HttpException("No permission", HttpStatus.FORBIDDEN);

        if (!classroomService.hasClasswork(classroomId, classworkId))
            throw new HttpException("Classwork not found", HttpStatus.BAD_REQUEST);

        var assignment = findByClassworkIdAndAuthorId(classworkId, studentId);

        if (assignment.getGrade() != null)
            throw new HttpException("Grade already exists", HttpStatus.BAD_REQUEST);

        var maxScore = assignment.getClasswork().getScore();

        if (createGradeDto.getGrade() <= 0 || createGradeDto.getGrade() >= maxScore)
            throw new HttpException("Grade must be between 0 and " + maxScore, HttpStatus.BAD_REQUEST);

        Grade grade = Grade.builder()
                .grade(createGradeDto.getGrade())
                .gradedBy(userService.findById(gradedBy))
                .comment(createGradeDto.getComment())
                .build();

        gradeRepository.save(grade);

        assignment.setGrade(grade);

        var savedAssignment = assignmentRepository.save(assignment);

        notificationProcessor.gradeDidCreate(savedAssignment);

        return savedAssignment.getGrade();
    }

    public Grade updateGrade(UpdateGradeDto updateGradeDto, String gradeId, String gradedBy) {
        var grade = gradeRepository.findByIdAndGradedById(gradeId, gradedBy)
                .orElseThrow(() -> new HttpException("Grade not found", HttpStatus.BAD_REQUEST));

        Optional.of(updateGradeDto.getGrade()).ifPresent(grade::setGrade);
        Optional.ofNullable(updateGradeDto.getComment()).ifPresent(grade::setComment);

        return gradeRepository.save(grade);
    }

    public boolean deleteGrade(String gradeId, String gradedBy) {
        var grade = gradeRepository.findByIdAndGradedById(gradeId, gradedBy)
                .orElseThrow(() -> new HttpException("Grade not found", HttpStatus.BAD_REQUEST));

        gradeRepository.delete(grade);

        return true;
    }
}
