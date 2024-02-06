package com.ht.elearning.user;

import com.ht.elearning.config.Response;
import com.ht.elearning.constants.ResponseMessage;
import com.ht.elearning.user.dtos.CreateUserDto;
import com.ht.elearning.user.dtos.UpdateUserDto;
import io.swagger.v3.oas.annotations.Operation;
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
    private final UserService userService;

    @Operation(summary = "Find a user by its id for admin only")
    @GetMapping("{userId}")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<Response<User>> findByUserId(@PathVariable String userId) {
        var user = userService.findById(userId);
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.OK,
                        true,
                        user
                )
        );
    }

    @Operation(summary = "Create a user for admin only")
    @PostMapping
    @PreAuthorize("hasAuthority('admin:create')")
    public ResponseEntity<Response<User>> create(@Valid @RequestBody CreateUserDto createUserDto) {
        var user = userService.create(createUserDto);
        return ResponseEntity.created(null).body(
                new Response<>(
                        HttpStatus.CREATED.value(),
                        ResponseMessage.CREATED,
                        true,
                        user
                )
        );
    }

    @Operation(summary = "Sync users to Elasticsearch for admin only")
    @PostMapping("/es/sync")
    @PreAuthorize("hasAuthority('admin:create')")
    public ResponseEntity<Response<?>> syncToElasticsearch() {
        userService.syncToElasticsearch();
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.UPDATED,
                        true,
                        null
                )
        );
    }

    @Operation(summary = "Update a user by its id for admin only")
    @PatchMapping("{userId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<Response<User>> update(@PathVariable String userId, @Valid @RequestBody UpdateUserDto updateUserDto) {
        User user = userService.update(userId, updateUserDto);
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.CREATED.value(),
                        ResponseMessage.UPDATED,
                        true,
                        user
                )
        );
    }

    @Operation(summary = "Delete a user by its id for admin only")
    @DeleteMapping("{userId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<Response<String>> delete(@PathVariable String userId) {
        var success = userService.deleteById(userId);
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.DELETED,
                        success,
                        userId
                )
        );
    }
}
