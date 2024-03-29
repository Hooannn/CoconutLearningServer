package com.ht.elearning.meeting;

import com.ht.elearning.config.Response;
import com.ht.elearning.constants.ResponseMessage;
import com.ht.elearning.meeting.dtos.CreateMeetingDto;
import com.ht.elearning.meeting.dtos.UpdateMeetingDto;
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
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/meeting")
public class MeetingController {
    private final MeetingService meetingService;

    @Operation(summary = "Generate a token for a meeting")
    @GetMapping("/token/{meetingId}")
    public ResponseEntity<Response<String>> generateMeetingToken(@PathVariable String meetingId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var token = meetingService.generateMeetingToken(meetingId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                Response.<String>builder()
                        .status(HttpStatus.OK.value())
                        .message(ResponseMessage.OK)
                        .data(token)
                        .success(true)
                        .build());
    }

    @Operation(summary = "Find a meeting by its id")
    @GetMapping("{meetingId}")
    public ResponseEntity<Response<Meeting>> findById(@PathVariable String meetingId) {
        var meeting = meetingService.findById(meetingId);
        return ResponseEntity.ok(
                Response.<Meeting>builder()
                        .status(HttpStatus.OK.value())
                        .message(ResponseMessage.OK)
                        .data(meeting)
                        .success(true)
                        .build());
    }

    @Operation(summary = "Find all meetings in a classroom")
    @GetMapping("/classroom/{classroomId}")
    public ResponseEntity<Response<List<Meeting>>> findAllByClassroomId(@PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var meetings = meetingService.findAllByClassroomId(classroomId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                Response.<List<Meeting>>builder()
                        .status(HttpStatus.OK.value())
                        .message(ResponseMessage.OK)
                        .data(meetings)
                        .success(true)
                        .build());
    }

    @Operation(summary = "Find all upcoming meetings in a classroom")
    @GetMapping("/classroom/{classroomId}/upcoming")
    public ResponseEntity<Response<List<Meeting>>> findUpcomingByClassroomId(@PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var meetings = meetingService.findUpcomingByClassroomId(classroomId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                Response.<List<Meeting>>builder()
                        .status(HttpStatus.OK.value())
                        .message(ResponseMessage.OK)
                        .data(meetings)
                        .success(true)
                        .build());
    }

    @Operation(summary = "Create a meeting in a classroom")
    @PostMapping("{classroomId}")
    public ResponseEntity<Response<Meeting>> create(@PathVariable String classroomId, @Valid @RequestBody CreateMeetingDto createMeetingDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var meeting = meetingService.create(createMeetingDto, classroomId, authentication.getPrincipal().toString());
        return ResponseEntity.created(null)
                .body(
                        Response.<Meeting>builder()
                                .status(HttpStatus.CREATED.value())
                                .message(ResponseMessage.CREATED)
                                .data(meeting)
                                .success(true)
                                .build());
    }

    @Operation(summary = "Update a meeting by its id")
    @PutMapping("{meetingId}")
    public ResponseEntity<Response<Meeting>> update(
            @PathVariable String meetingId,
            @Valid @RequestBody UpdateMeetingDto updateMeetingDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var meeting = meetingService.update(updateMeetingDto, meetingId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                Response.<Meeting>builder()
                        .status(HttpStatus.OK.value())
                        .message(ResponseMessage.UPDATED)
                        .data(meeting)
                        .success(true)
                        .build());
    }

    @Operation(summary = "Delete a meeting by its id")
    @DeleteMapping("{meetingId}")
    public ResponseEntity<Response<Meeting>> deleteById(@PathVariable String meetingId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = meetingService.deleteById(meetingId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                Response.<Meeting>builder()
                        .status(HttpStatus.OK.value())
                        .message(ResponseMessage.DELETED)
                        .success(success)
                        .build());
    }
}
