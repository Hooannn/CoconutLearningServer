package com.ht.elearning.assignment;

import com.ht.elearning.assignment.dtos.CreateAssignmentDto;
import com.ht.elearning.assignment.dtos.CreateGradeDto;
import com.ht.elearning.assignment.dtos.UpdateAssignmentDto;
import com.ht.elearning.assignment.dtos.UpdateGradeDto;
import com.ht.elearning.config.Response;
import com.ht.elearning.constants.ResponseMessage;
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
@RequestMapping(path = "/api/v1/assignments")
@CrossOrigin
public class AssignmentController {
    private final AssignmentService assignmentService;

    @Operation(summary = "Create an assignment")
    @PostMapping
    public ResponseEntity<Response<Assignment>> create(@Valid @RequestBody CreateAssignmentDto createAssignmentDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var assignment = assignmentService.create(createAssignmentDto, authentication.getPrincipal().toString());

        return ResponseEntity.created(null).body(
                new Response<>(
                        HttpStatus.CREATED.value(),
                        ResponseMessage.SUBMITTED,
                        true,
                        assignment
                )
        );
    }

    @Operation(summary = "Create a grade for an assignment")
    @PostMapping("/{classroomId}/{classworkId}/{studentId}/grade")
    public ResponseEntity<Response<Grade>> createGrade(@PathVariable String classworkId,
                                                       @PathVariable String classroomId,
                                                       @PathVariable String studentId,
                                                       @Valid @RequestBody CreateGradeDto createGradeDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var grade = assignmentService.createGrade(createGradeDto, classworkId, classroomId, studentId, authentication.getPrincipal().toString());

        return ResponseEntity.created(null).body(
                new Response<>(
                        HttpStatus.CREATED.value(),
                        ResponseMessage.GRADED,
                        true,
                        grade
                )
        );
    }

    @Operation(summary = "Update a grade for an assignment by gradeId")
    @PutMapping("/grade/{gradeId}")
    public ResponseEntity<Response<Grade>> updateGrade(@PathVariable String gradeId,
                                                       @Valid @RequestBody UpdateGradeDto updateGradeDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var grade = assignmentService.updateGrade(updateGradeDto, gradeId, authentication.getPrincipal().toString());

        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.UPDATED,
                        true,
                        grade
                )
        );
    }

    @Operation(summary = "Delete a grade for an assignment by gradeId")
    @DeleteMapping("/grade/{gradeId}")
    public ResponseEntity<Response<?>> deleteGrade(@PathVariable String gradeId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var grade = assignmentService.deleteGrade(gradeId, authentication.getPrincipal().toString());

        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.DELETED,
                        grade,
                        null
                )
        );
    }

    @Operation(summary = "Update an assignment")
    @PutMapping("/{classroomId}/{classworkId}")
    public ResponseEntity<Response<Assignment>> update(@Valid @RequestBody UpdateAssignmentDto updateAssignmentDto,
                                                       @PathVariable String classworkId,
                                                       @PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var assignment = assignmentService.update(updateAssignmentDto, classworkId, classroomId, authentication.getPrincipal().toString());

        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.UPDATED,
                        true,
                        assignment
                )
        );
    }

    @Operation(summary = "Mark an assignment as submitted")
    @PutMapping("/{classroomId}/{classworkId}/mark/submitted")
    public ResponseEntity<Response<Assignment>> markAssignmentAsSubmitted(@PathVariable String classworkId,
                                                                          @PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var assignment = assignmentService.updateSubmittedStatus(true, classworkId, classroomId, authentication.getPrincipal().toString());

        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.SUBMITTED,
                        true,
                        assignment
                )
        );
    }

    @Operation(summary = "Mark an assignment as unsubmitted")
    @PutMapping("/{classroomId}/{classworkId}/mark/unsubmitted")
    public ResponseEntity<Response<Assignment>> markAssignmentAsUnsubmitted(@PathVariable String classworkId,
                                                                            @PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var assignment = assignmentService.updateSubmittedStatus(false, classworkId, classroomId, authentication.getPrincipal().toString());

        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.UPDATED,
                        true,
                        assignment
                )
        );
    }

    @Operation(summary = "Count submitted assignments for a classwork")
    @GetMapping("/{classroomId}/{classworkId}/count/submitted")
    public ResponseEntity<Response<Long>> countSubmitted(@PathVariable String classworkId,
                                                         @PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var count = assignmentService.countSubmitted(classworkId, classroomId, authentication.getPrincipal().toString());

        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.OK,
                        true,
                        count
                )
        );
    }

    @Operation(summary = "Find all assignments by classwork")
    @GetMapping("/{classroomId}/{classworkId}")
    public ResponseEntity<Response<List<Assignment>>> findByClassworkId(@PathVariable String classworkId,
                                                                        @PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var assignments = assignmentService.findAllByClassworkId(classworkId, classroomId, authentication.getPrincipal().toString());

        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.OK,
                        true,
                        assignments
                )
        );
    }

    @Operation(summary = "Find an assignment by classwork and student")
    @GetMapping("/{classroomId}/{classworkId}/{authorId}")
    public ResponseEntity<Response<Assignment>> findByClassworkIdAndAuthorId(@PathVariable String classworkId,
                                                                             @PathVariable String classroomId,
                                                                             @PathVariable String authorId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var assignment = assignmentService.findByClassworkIdAndAuthorId(classworkId, classroomId, authorId, authentication.getPrincipal().toString());

        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.OK,
                        true,
                        assignment
                )
        );
    }

    @Operation(summary = "Delete an assignment by classwork and student")
    @DeleteMapping("/{classroomId}/{classworkId}")
    public ResponseEntity<Response<?>> deleteByClassworkIdAndAuthorId(@PathVariable String classworkId,
                                                                      @PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = assignmentService.deleteByClassworkIdAndAuthorId(classworkId, classroomId, authentication.getPrincipal().toString());

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
