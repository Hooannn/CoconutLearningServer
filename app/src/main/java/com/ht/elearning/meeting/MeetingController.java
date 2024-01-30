package com.ht.elearning.meeting;

import com.ht.elearning.config.Response;
import com.ht.elearning.constants.ResponseMessage;
import com.ht.elearning.meeting.dtos.CreateMeetingDto;
import com.ht.elearning.meeting.dtos.UpdateMeetingDto;
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
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/meeting")
public class MeetingController {
    private final MeetingService meetingService;

    @GetMapping(path = "/token/test")
    public ResponseEntity<Response<String>> generateTestToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var token = meetingService.generateTestToken(authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                Response.<String>builder()
                        .status(HttpStatus.OK.value())
                        .message(ResponseMessage.OK)
                        .data(token)
                        .success(true)
                        .build()
        );
    }

    @GetMapping("/token/{meetingId}")
    public ResponseEntity<Response<String>> generateMeetingToken(@PathVariable String meetingId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var token = meetingService.generateMeetingToken(meetingId, authentication.getPrincipal().toString());
        System.out.println(token);
        return ResponseEntity.ok(
                Response.<String>builder()
                        .status(HttpStatus.OK.value())
                        .message(ResponseMessage.OK)
                        .data(token)
                        .success(true)
                        .build());
    }

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
