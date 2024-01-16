package com.ht.elearning.assignment;

import com.ht.elearning.assignment.dtos.CreateAssignmentDto;
import com.ht.elearning.assignment.dtos.UpdateAssignmentDto;
import com.ht.elearning.config.Response;
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
@RequestMapping(path = "/api/v1/assignments")
@CrossOrigin
public class AssignmentController {
    private final AssignmentService assignmentService;

    @PostMapping
    public ResponseEntity<Response<Assignment>> create(@Valid @RequestBody CreateAssignmentDto createAssignmentDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var assignment = assignmentService.create(createAssignmentDto, authentication.getPrincipal().toString());

        return ResponseEntity.created(null).body(
                new Response<>(
                        HttpStatus.CREATED.value(),
                        "Submitted successfully",
                        true,
                        assignment
                )
        );
    }

    @PutMapping("/{classroomId}/{classworkId}")
    public ResponseEntity<Response<Assignment>> update(@Valid @RequestBody UpdateAssignmentDto updateAssignmentDto,
                                                       @PathVariable String classworkId,
                                                       @PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var assignment = assignmentService.update(updateAssignmentDto, classworkId, classroomId, authentication.getPrincipal().toString());

        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Updated successfully",
                        true,
                        assignment
                )
        );
    }

    @PutMapping("/{classroomId}/{classworkId}/mark/submitted")
    public ResponseEntity<Response<Assignment>> markAssignmentAsSubmitted(@PathVariable String classworkId,
                                                                          @PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var assignment = assignmentService.updateSubmittedStatus(true, classworkId, classroomId, authentication.getPrincipal().toString());

        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Submitted successfully",
                        true,
                        assignment
                )
        );
    }

    @PutMapping("/{classroomId}/{classworkId}/mark/unsubmitted")
    public ResponseEntity<Response<Assignment>> markAssignmentAsUnsubmitted(@PathVariable String classworkId,
                                                                            @PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var assignment = assignmentService.updateSubmittedStatus(false, classworkId, classroomId, authentication.getPrincipal().toString());

        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Updated successfully",
                        true,
                        assignment
                )
        );
    }

    @GetMapping("/{classroomId}/{classworkId}/count/submitted")
    public ResponseEntity<Response<Long>> countSubmitted(@PathVariable String classworkId,
                                                         @PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var count = assignmentService.countSubmitted(classworkId, classroomId, authentication.getPrincipal().toString());

        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Ok",
                        true,
                        count
                )
        );
    }

    @GetMapping("/{classroomId}/{classworkId}")
    public ResponseEntity<Response<List<Assignment>>> findByClassworkId(@PathVariable String classworkId,
                                                                        @PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var assignments = assignmentService.findAllByClassworkId(classworkId, classroomId, authentication.getPrincipal().toString());

        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Ok",
                        true,
                        assignments
                )
        );
    }

    @GetMapping("/{classroomId}/{classworkId}/{authorId}")
    public ResponseEntity<Response<Assignment>> findByClassworkIdAndAuthorId(@PathVariable String classworkId,
                                                                             @PathVariable String classroomId,
                                                                             @PathVariable String authorId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var assignment = assignmentService.findByClassworkIdAndAuthorId(classworkId, classroomId, authorId, authentication.getPrincipal().toString());

        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Ok",
                        true,
                        assignment
                )
        );
    }

    @DeleteMapping("/{classroomId}/{classworkId}")
    public ResponseEntity<Response<?>> deleteByClassworkIdAndAuthorId(@PathVariable String classworkId,
                                                                      @PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = assignmentService.deleteByClassworkIdAndAuthorId(classworkId, classroomId, authentication.getPrincipal().toString());

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
