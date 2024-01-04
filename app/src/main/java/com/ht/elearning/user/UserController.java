package com.ht.elearning.user;

import com.ht.elearning.config.Response;
import com.ht.elearning.user.dtos.CreateUserDto;
import com.ht.elearning.user.dtos.UpdateUserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/api/v1/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin
public class UserController {
    private final UserService service;

    @GetMapping("/authenticated")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Response<User>> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var user = service.findById(authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Ok",
                        true,
                        user
                )
        );
    }


    @GetMapping("{userId}")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<Response<User>> findByUserId(@PathVariable String userId) {
        var user = service.findById(userId);
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Ok",
                        true,
                        user
                )
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('admin:create')")
    public ResponseEntity<Response<User>> create(@Valid @RequestBody CreateUserDto createUserDto) {
        var user = service.create(createUserDto);
        return ResponseEntity.created(null).body(
                new Response<>(
                        HttpStatus.CREATED.value(),
                        "Created",
                        true,
                        user
                )
        );
    }

    @PatchMapping("{userId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<Response<User>> update(@PathVariable String userId, @Valid @RequestBody UpdateUserDto updateUserDto) {
        User user = service.update(userId, updateUserDto);
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.CREATED.value(),
                        "Updated",
                        true,
                        user
                )
        );
    }


    @DeleteMapping("{userId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<Response<String>> delete(@PathVariable String userId) {
        var success = service.deleteById(userId);
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Deleted",
                        success,
                        userId
                )
        );
    }
}
