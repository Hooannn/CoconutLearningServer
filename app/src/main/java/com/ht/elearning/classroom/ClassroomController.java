package com.ht.elearning.classroom;


import com.ht.elearning.classroom.dtos.*;
import com.ht.elearning.config.Response;
import com.ht.elearning.constants.ResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/classrooms")
@CrossOrigin
public class ClassroomController {
    private final ClassroomService classroomService;

    @Operation(summary = "Find all user's teaching classrooms")
    @GetMapping("teaching")
    public ResponseEntity<Response<List<Classroom>>> findTeachingClassrooms() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classrooms = classroomService.findTeachingClassrooms(authentication.getPrincipal().toString());

        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.OK,
                        true,
                        classrooms
                )
        );
    }

    @Operation(summary = "Find all user's registered classrooms")
    @GetMapping("registered")
    public ResponseEntity<Response<List<Classroom>>> findRegisteredClassrooms() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classrooms = classroomService.findRegisteredClassrooms(authentication.getPrincipal().toString());

        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.OK,
                        true,
                        classrooms
                )
        );
    }

    @Operation(summary = "Create a new classroom")
    @PostMapping
    public ResponseEntity<Response<Classroom>> create(@Valid @RequestBody CreateClassroomDto createClassroomDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classroom = classroomService.create(createClassroomDto, authentication.getPrincipal().toString());

        return ResponseEntity.created(null).body(
                new Response<>(
                        HttpStatus.CREATED.value(),
                        ResponseMessage.CREATED,
                        true,
                        classroom
                )
        );
    }

    @Operation(summary = "Find classroom by its id")
    @GetMapping("{classroomId}")
    public ResponseEntity<Response<Classroom>> findByClassroomId(@PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classroom = classroomService.find(classroomId, authentication.getPrincipal().toString());

        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.OK,
                        true,
                        classroom
                )
        );
    }

    @Operation(summary = "Update classroom by its id")
    @PutMapping("{classroomId}")
    public ResponseEntity<Response<?>> update(@Valid @RequestBody UpdateClassroomDto updateClassroomDto, @PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classroom = classroomService.update(updateClassroomDto, classroomId, authentication.getPrincipal().toString());

        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.UPDATED,
                        true,
                        classroom
                )
        );
    }

    @Operation(summary = "Delete classroom by its id")
    @DeleteMapping("{classroomId}")
    public ResponseEntity<Response<?>> deleteById(@PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = classroomService.deleteById(classroomId, authentication.getPrincipal().toString());

        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.DELETED,
                        success,
                        null
                )
        );
    }

    @Operation(summary = "Remove member from classroom")
    @PostMapping("/{classroomId}/remove/member/{memberId}")
    public ResponseEntity<Response<?>> removeMember(@PathVariable String memberId, @PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = classroomService.removeMember(classroomId, memberId, authentication.getPrincipal().toString());

        return ResponseEntity.created(null).body(
                new Response<>(
                        HttpStatus.CREATED.value(),
                        ResponseMessage.REMOVED,
                        success,
                        null
                )
        );
    }

    @Operation(summary = "Create an invite to join classroom")
    @PostMapping("/invite")
    public ResponseEntity<Response<?>> invite(@Valid @RequestBody InviteDto inviteDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = classroomService.invite(inviteDto, authentication.getPrincipal().toString());

        return ResponseEntity.created(null).body(
                new Response<>(
                        HttpStatus.CREATED.value(),
                        ResponseMessage.INVITED,
                        success,
                        null
                )
        );
    }

    @Operation(summary = "Create many invites to join classroom")
    @PostMapping("/invite/many")
    public ResponseEntity<Response<?>> inviteMany(@Valid @RequestBody InviteManyDto inviteManyDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = classroomService.inviteMany(inviteManyDto, authentication.getPrincipal().toString());

        return ResponseEntity.created(null).body(
                new Response<>(
                        HttpStatus.CREATED.value(),
                        ResponseMessage.INVITED,
                        success,
                        null
                )
        );
    }

    @Operation(summary = "Remove an existing invite")
    @PostMapping("/invite/remove/{classroomId}")
    public ResponseEntity<Response<?>> removeInvite(@Valid @RequestBody RemoveInviteDto removeInviteDto,
                                                    @PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = classroomService.removeInvite(removeInviteDto, classroomId, authentication.getPrincipal().toString());

        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.INVITE_REMOVED,
                        success,
                        null
                )
        );
    }

    @Operation(summary = "Join classroom by invite code")
    @PostMapping("/join/{inviteCode}")
    public ResponseEntity<Response<Classroom>> join(@PathVariable String inviteCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = classroomService.join(inviteCode, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.CLASSROOM_JOINED,
                        success,
                        null
                )
        );
    }

    @Operation(summary = "Accept an invite to join classroom")
    @PostMapping("/webhook/accept/{inviteCode}")
    public ResponseEntity<Response<?>> accept(@PathVariable String inviteCode, @RequestParam String notificationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = classroomService.accept(inviteCode, notificationId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.INVITE_ACCEPTED,
                        success,
                        null
                )
        );
    }

    @Operation(summary = "Refuse an invite to join classroom")
    @PostMapping("/webhook/refuse/{inviteCode}")
    public ResponseEntity<Response<?>> refuse(@PathVariable String inviteCode, @RequestParam String notificationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = classroomService.refuse(inviteCode, notificationId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.INVITE_DECLINED,
                        success,
                        null
                )
        );
    }

    @Operation(summary = "Leave classroom")
    @PostMapping("/leave/{classroomId}")
    public ResponseEntity<Response<?>> leave(@PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = classroomService.leave(classroomId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.CLASSROOM_LEFT,
                        success,
                        null
                )
        );
    }

    @Operation(summary = "Reset classroom invite code")
    @PostMapping("/{classroomId}/class_code/reset")
    public ResponseEntity<Response<Classroom>> resetClassCode(@PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classroom = classroomService.resetClassCode(classroomId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.CLASSROOM_CODE_RESET,
                        true,
                        classroom
                )
        );
    }
}
