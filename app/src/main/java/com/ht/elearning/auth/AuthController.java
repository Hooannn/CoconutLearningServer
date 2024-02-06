package com.ht.elearning.auth;

import com.ht.elearning.auth.dtos.*;
import com.ht.elearning.auth.dtos.ResendAccountVerificationDto;
import com.ht.elearning.config.Response;
import com.ht.elearning.constants.ResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<Response<AuthenticationResponse>> register(@Valid @RequestBody RegisterDto registerDto) {
        var authenticationResponse = authService.register(registerDto);
        return ResponseEntity.created(null).body(
                new Response<>(
                        HttpStatus.CREATED.value(),
                        ResponseMessage.REGISTERED,
                        true,
                        authenticationResponse
                )
        );
    }

    @Operation(summary = "Authenticate a user")
    @PostMapping("/authenticate")
    public ResponseEntity<Response<AuthenticationResponse>> authenticate(@Valid @RequestBody AuthenticateDto authenticateDto) {
        var authenticationResponse = authService.authenticate(authenticateDto);
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.AUTHENTICATED,
                        true,
                        authenticationResponse
                )
        );
    }

    @Operation(summary = "Authenticate a user with Google")
    @PostMapping("/google")
    public ResponseEntity<Response<AuthenticationResponse>> authenticateWithGoogle(@Valid @RequestBody GoogleAuthDto googleAuthDto) {
        var authenticationResponse = authService.authenticateWithGoogle(googleAuthDto);
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.AUTHENTICATED,
                        true,
                        authenticationResponse
                )
        );
    }

    @Operation(summary = "Verify a user's account with the token sent to their email address")
    @PostMapping("/verify-account")
    public ResponseEntity<Response<AuthenticationResponse>> verifyAccount(@Valid @RequestBody VerifyAccountDto verifyAccountDto) {
        var authenticationResponse = authService.verifyAccount(verifyAccountDto);
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.VERIFIED,
                        true,
                        authenticationResponse
                )
        );
    }

    @Operation(summary = "Resend the account verification token to the user's email address")
    @PostMapping("/verify-account/resend")
    public ResponseEntity<Response<?>> resendAccountVerification(@RequestBody ResendAccountVerificationDto resendAccountVerificationDto) {
        var success = authService.resendAccountVerification(resendAccountVerificationDto);
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.RESENT,
                        success,
                        null
                )
        );
    }

    @Operation(summary = "Send a password reset token to the user's email address")
    @PostMapping("/forgot-password")
    public ResponseEntity<Response<?>> forgotPassword(@Valid @RequestBody ForgotPasswordDto forgotPasswordDto) {
        boolean success = authService.forgotPassword(forgotPasswordDto);
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.REQUESTED,
                        success,
                        null
                )
        );
    }

    @Operation(summary = "Reset the user's password with the token sent to their email address")
    @PostMapping("/reset-password")
    public ResponseEntity<Response<?>> resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto) {
        boolean success = authService.resetPassword(resetPasswordDto);
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.PASSWORD_UPDATED,
                        success,
                        null
                )
        );
    }

    @Operation(summary = "Refresh the user's credentials with the refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<Response<Credentials>> refresh(@Valid @RequestBody RefreshDto refreshDto) {
        var credentials = authService.refresh(refreshDto);
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.REFRESHED,
                        true,
                        credentials
                )
        );
    }

    @Operation(summary = "Sign out the user")
    @PostMapping("/sign-out")
    public ResponseEntity<Response<?>> signOut(@Valid @RequestBody SignOutDto signOutDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = authService.signOut(signOutDto, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.SIGNED_OUT,
                        success,
                        null
                )
        );
    }
}
