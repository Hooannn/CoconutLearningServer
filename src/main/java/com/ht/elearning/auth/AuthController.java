package com.ht.elearning.auth;

import com.ht.elearning.auth.dtos.AuthenticateDto;
import com.ht.elearning.auth.dtos.RegisterDto;
import com.ht.elearning.auth.dtos.VerifyAccountDto;
import com.ht.elearning.config.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController()
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<Response<AuthenticationResponse>> register(@Valid @RequestBody RegisterDto registerDto) {
        var authenticationResponse = authService.register(registerDto);
        return ResponseEntity.created(null).body(
                new Response<>(
                        HttpStatus.CREATED.value(),
                        "Registered successfully. Please check your email to verify your registration",
                        true,
                        authenticationResponse
                )
        );
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Response<AuthenticationResponse>> authenticate(@Valid @RequestBody AuthenticateDto authenticateDto) {
        var authenticationResponse = authService.authenticate(authenticateDto);
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Authenticated successfully",
                        true,
                        authenticationResponse
                )
        );
    }

    @PostMapping("/verify-account")
    public ResponseEntity<Response<?>> verifyAccount(@Valid @RequestBody VerifyAccountDto verifyAccountDto) {
        boolean success = authService.verifyAccount(verifyAccountDto);
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Your account has verified successfully",
                        success,
                        null
                )
        );
    }


    /*
    @PostMapping("/refresh")
    public ResponseEntity<Response<AuthenticationResponse>> refresh(@Valid @RequestBody AuthenticateDto authenticateDto) {

    }
    */
}
