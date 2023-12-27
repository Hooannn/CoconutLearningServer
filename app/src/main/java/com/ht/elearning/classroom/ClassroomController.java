package com.ht.elearning.classroom;


import com.ht.elearning.classroom.dtos.CreateClassroomDto;
import com.ht.elearning.classroom.dtos.InviteDto;
import com.ht.elearning.classroom.dtos.JoinDto;
import com.ht.elearning.config.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/classrooms")
public class ClassroomController {
    private final ClassroomService service;
    @PostMapping
    @PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<Response<Classroom>> createClassroom(@Valid @RequestBody CreateClassroomDto createClassroomDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classroom = service.createClassroom(createClassroomDto, authentication.getPrincipal().toString());

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
    public ResponseEntity<Response<Classroom>> findClassroom(@PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classroom = service.findClassroom(classroomId, authentication.getPrincipal().toString());

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


    @PostMapping("/join")
    public ResponseEntity<Response<Classroom>> join(@Valid @RequestBody JoinDto joinDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = service.join(joinDto, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Successfully joined classroom",
                        success,
                        null
                )
        );
    }
}
