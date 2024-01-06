package com.ht.elearning.classroom;


import com.ht.elearning.classroom.dtos.CreateClassroomDto;
import com.ht.elearning.classroom.dtos.InviteDto;
import com.ht.elearning.classroom.dtos.RemoveInviteDto;
import com.ht.elearning.classroom.dtos.UpdateClassroomDto;
import com.ht.elearning.config.Response;
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
    private final ClassroomService service;


    @GetMapping("teaching")
    public ResponseEntity<Response<List<Classroom>>> findTeachingClassrooms() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classrooms = service.findTeachingClassrooms(authentication.getPrincipal().toString());

        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Ok",
                        true,
                        classrooms
                )
        );
    }


    @GetMapping("registered")
    public ResponseEntity<Response<List<Classroom>>> findRegisteredClassrooms() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classrooms = service.findRegisteredClassrooms(authentication.getPrincipal().toString());

        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Ok",
                        true,
                        classrooms
                )
        );
    }


    @PostMapping
    public ResponseEntity<Response<Classroom>> create(@Valid @RequestBody CreateClassroomDto createClassroomDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classroom = service.create(createClassroomDto, authentication.getPrincipal().toString());

        return ResponseEntity.created(null).body(
                new Response<>(
                        HttpStatus.CREATED.value(),
                        "Created",
                        true,
                        classroom
                )
        );
    }

    @GetMapping("{classroomId}")
    public ResponseEntity<Response<Classroom>> findByClassroomId(@PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classroom = service.find(classroomId, authentication.getPrincipal().toString());

        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Ok",
                        true,
                        classroom
                )
        );
    }

    @PutMapping("{classroomId}")
    public ResponseEntity<Response<?>> update(@Valid @RequestBody UpdateClassroomDto updateClassroomDto, @PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classroom = service.update(updateClassroomDto, classroomId, authentication.getPrincipal().toString());

        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Updated successfully",
                        true,
                        classroom
                )
        );
    }

    @DeleteMapping("{classroomId}")
    public ResponseEntity<Response<?>> deleteById(@PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = service.deleteById(classroomId, authentication.getPrincipal().toString());

        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Deleted",
                        success,
                        null
                )
        );
    }

    @PostMapping("/{classroomId}/remove/member/{memberId}")
    public ResponseEntity<Response<?>> removeMember(@PathVariable String memberId, @PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = service.removeMember(classroomId, memberId, authentication.getPrincipal().toString());

        return ResponseEntity.created(null).body(
                new Response<>(
                        HttpStatus.CREATED.value(),
                        "Removed",
                        success,
                        null
                )
        );
    }

    @PostMapping("/invite")
    public ResponseEntity<Response<?>> invite(@Valid @RequestBody InviteDto inviteDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = service.invite(inviteDto, authentication.getPrincipal().toString());

        return ResponseEntity.created(null).body(
                new Response<>(
                        HttpStatus.CREATED.value(),
                        "Invited",
                        success,
                        null
                )
        );
    }

    @PostMapping("/invite/remove/{classroomId}")
    public ResponseEntity<Response<?>> removeInvite(@Valid @RequestBody RemoveInviteDto removeInviteDto,
                                                    @PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = service.removeInvite(removeInviteDto, classroomId, authentication.getPrincipal().toString());

        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Removed invite successfully",
                        success,
                        null
                )
        );
    }

    @PostMapping("/join/{inviteCode}")
    public ResponseEntity<Response<Classroom>> join(@PathVariable String inviteCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = service.join(inviteCode, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Successfully joined classroom",
                        success,
                        null
                )
        );
    }

    @PostMapping("/accept/{inviteCode}")
    public ResponseEntity<Response<Classroom>> accept(@PathVariable String inviteCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = service.accept(inviteCode, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Accepted",
                        success,
                        null
                )
        );
    }

    @PostMapping("/refuse/{inviteCode}")
    public ResponseEntity<Response<Classroom>> refuse(@PathVariable String inviteCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = service.refuse(inviteCode, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Successfully refused to join classroom",
                        success,
                        null
                )
        );
    }

    @PostMapping("/leave/{classroomId}")
    public ResponseEntity<Response<Classroom>> leave(@PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = service.leave(classroomId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Successfully left classroom",
                        success,
                        null
                )
        );
    }

    @PostMapping("/{classroomId}/class_code/reset")
    public ResponseEntity<Response<Classroom>> resetClassCode(@PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classroom = service.resetClassCode(classroomId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Successfully reset class code",
                        true,
                        classroom
                )
        );
    }
}
