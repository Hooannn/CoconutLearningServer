package com.ht.elearning.user;

import com.ht.elearning.config.Response;
import com.ht.elearning.constants.ResponseMessage;
import com.ht.elearning.user.dtos.UpdatePasswordDto;
import com.ht.elearning.user.dtos.UpdateProfileDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping(path = "/api/v1/profile")
public class ProfileController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Response<User>> find() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var user = userService.findById(authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.OK,
                        true,
                        user
                )
        );
    }

    @PutMapping
    public ResponseEntity<Response<User>> update(@Valid @RequestBody UpdateProfileDto updateProfileDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var user = userService.updateProfile(updateProfileDto, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.UPDATED,
                        true,
                        user
                )
        );
    }

    @PutMapping("/password")
    public ResponseEntity<Response<User>> updatePassword(@Valid @RequestBody UpdatePasswordDto updatePasswordDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var user = userService.updatePassword(updatePasswordDto, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.PASSWORD_UPDATED,
                        true,
                        user
                )
        );
    }
}
