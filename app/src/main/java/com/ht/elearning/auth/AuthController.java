package com.ht.elearning.auth;

import com.ht.elearning.auth.dtos.*;
import com.ht.elearning.auth.dtos.ResendAccountVerificationDto;
import com.ht.elearning.config.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController()
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin
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


    @PostMapping("/google")
    public ResponseEntity<Response<AuthenticationResponse>> authenticateWithGoogle(@Valid @RequestBody GoogleAuthDto googleAuthDto) {
        var authenticationResponse = authService.authenticateWithGoogle(googleAuthDto);
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
    public ResponseEntity<Response<AuthenticationResponse>> verifyAccount(@Valid @RequestBody VerifyAccountDto verifyAccountDto) {
        var authenticationResponse = authService.verifyAccount(verifyAccountDto);
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Your account has verified successfully",
                        true,
                        authenticationResponse
                )
        );
    }


    @PostMapping("/verify-account/resend")
    public ResponseEntity<Response<?>> resendAccountVerification(@RequestBody ResendAccountVerificationDto resendAccountVerificationDto) {
        var success = authService.resendAccountVerification(resendAccountVerificationDto);
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Resent successfully",
                        success,
                        null
                )
        );
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<Response<?>> forgotPassword(@Valid @RequestBody ForgotPasswordDto forgotPasswordDto) {
        boolean success = authService.forgotPassword(forgotPasswordDto);
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Your request is being processed",
                        success,
                        null
                )
        );
    }


    @PostMapping("/reset-password")
    public ResponseEntity<Response<?>> resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto) {
        boolean success = authService.resetPassword(resetPasswordDto);
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Your password has been updated",
                        success,
                        null
                )
        );
    }


    @PostMapping("/refresh")
    public ResponseEntity<Response<Credentials>> refresh(@Valid @RequestBody RefreshDto refreshDto) {
        var credentials = authService.refresh(refreshDto);
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Refreshed successfully",
                        true,
                        credentials
                )
        );
    }


    @PostMapping("/sign-out")
    public ResponseEntity<Response<?>> signOut(@Valid @RequestBody SignOutDto signOutDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = authService.signOut(signOutDto, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Signed out",
                        success,
                        null
                )
        );
    }
}
