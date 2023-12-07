package com.ht.elearning.auth;

import com.ht.elearning.auth.dtos.AuthenticateDto;
import com.ht.elearning.auth.dtos.RegisterDto;
import com.ht.elearning.auth.dtos.VerifyAccountDto;
import com.ht.elearning.config.HttpException;
import com.ht.elearning.config.QueryResponse;
import com.ht.elearning.jwt.JwtService;
import com.ht.elearning.mail.MailService;
import com.ht.elearning.redis.RedisService;
import com.ht.elearning.user.Role;
import com.ht.elearning.user.User;
import com.ht.elearning.user.UserRepository;
import com.ht.elearning.utils.Helper;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;
    private final AuthProcessing authProcessing;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RedisService redisService;

    public AuthenticationResponse register(RegisterDto registerDto) {
        try {
            if (userRepository.existsByEmail(registerDto.getEmail())) {
                throw new HttpException("Email is already registered", HttpStatus.BAD_REQUEST);
            }
            var user = User.builder()
                    .firstName(registerDto.getFirstName())
                    .lastName(registerDto.getLastName())
                    .email(registerDto.getEmail())
                    .password(passwordEncoder.encode(registerDto.getPassword()))
                    .role(Role.USER)
                    .build();
            var savedUser = userRepository.save(user);
            authProcessing.processAccountVerification(savedUser);
            return AuthenticationResponse
                    .builder()
                    .credentials(getCredentials(savedUser))
                    .user(savedUser)
                    .build();
        } catch (Exception e) {
            throw new HttpException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public AuthenticationResponse authenticate(AuthenticateDto authenticateDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticateDto.getEmail(),
                        authenticateDto.getPassword()
                )
        );
        var user = userRepository.findByEmail(authenticateDto.getEmail()).orElseThrow();
        return AuthenticationResponse
                .builder()
                .credentials(getCredentials(user))
                .user(user)
                .build();
    }


    public boolean verifyAccount(VerifyAccountDto verifyAccountDto) {
        String signature = verifyAccountDto.getSignature();
        String email = verifyAccountDto.getEmail();
        var user = userRepository.findByEmail(email).orElseThrow(() -> new HttpException(
                "Invalid signature",
                HttpStatus.FORBIDDEN
        ));
        if (user.isVerified()) {
            throw new HttpException("Request is not acceptable", HttpStatus.NOT_ACCEPTABLE);
        }
        String validSignature = redisService.getValue("account_signature:" + email);
        if (signature.equals(validSignature)) {
            user.setVerified(true);
            userRepository.save(user);
            try {
                authProcessing.processWelcomeUser(user);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return true;
        }
        throw new HttpException("Invalid signature", HttpStatus.FORBIDDEN);
    }

    private Credentials getCredentials(User user) {
        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        redisService.setValue("refresh_token:" + user.getId(), refreshToken, refreshExpiration / 1000);
        return Credentials
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
