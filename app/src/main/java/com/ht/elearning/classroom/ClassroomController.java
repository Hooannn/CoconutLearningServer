package com.ht.elearning.classroom;


import com.ht.elearning.classroom.dtos.CreateClassroomDto;
import com.ht.elearning.classroom.dtos.InviteDto;
import com.ht.elearning.config.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/classrooms")
@CrossOrigin
public class ClassroomController {
    private final ClassroomService service;

    @PostMapping
    @PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
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

    @PostMapping("/invite")
    @PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
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
