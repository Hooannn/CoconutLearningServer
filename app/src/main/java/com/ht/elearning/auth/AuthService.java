package com.ht.elearning.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ht.elearning.auth.dtos.*;
import com.ht.elearning.config.HttpException;
import com.ht.elearning.constants.ErrorMessage;
import com.ht.elearning.jwt.JwtService;
import com.ht.elearning.processor.AppProcessor;
import com.ht.elearning.push_notification.PushNotificationService;
import com.ht.elearning.push_notification.dtos.RemoveTokenDto;
import com.ht.elearning.redis.RedisService;
import com.ht.elearning.user.Role;
import com.ht.elearning.user.User;
import com.ht.elearning.user.UserRepository;
import com.ht.elearning.user.UserService;
import com.ht.elearning.utils.Helper;
import com.ht.elearning.utils.MD5;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final AppProcessor appProcessor;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RedisService redisService;
    private final PushNotificationService pushNotificationService;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;


    public AuthenticationResponse register(RegisterDto registerDto) {
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new HttpException(ErrorMessage.USER_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        }
        String hash = MD5.md5Hex(registerDto.getEmail().toLowerCase());
        String defaultAvatarUrl = "https://gravatar.com/avatar/" + hash;
        var user = User.builder()
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .email(registerDto.getEmail())
                .avatarUrl(defaultAvatarUrl)
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .role(Role.USER)
                .build();
        var savedUser = userService.save(user);
        try {
            appProcessor.userDidCreate(savedUser);
        } catch (MessagingException e) {
            logger.error("Error while processing account verification - Email[{}] - Message[{}]", user.getEmail(), e.getMessage());
        }
        return AuthenticationResponse
                .builder()
                .credentials(null)
                .user(savedUser)
                .build();
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


    public AuthenticationResponse verifyAccount(VerifyAccountDto verifyAccountDto) {
        var signature = verifyAccountDto.getSignature();
        var email = verifyAccountDto.getEmail();
        var user = userRepository.findByEmail(email).orElseThrow(() -> new HttpException(
                ErrorMessage.INVALID_CREDENTIALS,
                HttpStatus.FORBIDDEN
        ));
        if (user.isVerified()) {
            throw new HttpException(ErrorMessage.REQUEST_NOT_ACCEPTABLE, HttpStatus.NOT_ACCEPTABLE);
        }
        var validSignature = redisService.getValue("account_signature:" + email);
        if (signature.equals(validSignature)) {
            redisService.deleteValue("account_signature:" + email);
            user.setVerified(true);
            var savedUser = userService.save(user);
            try {
                appProcessor.userDidVerify(user);
            } catch (MessagingException e) {
                logger.error("Error while processing welcome user - Email[{}] - Message[{}]", user.getEmail(), e.getMessage());
            }
            return AuthenticationResponse
                    .builder()
                    .credentials(getCredentials(savedUser))
                    .user(savedUser)
                    .build();
        }
        throw new HttpException(ErrorMessage.INVALID_CREDENTIALS, HttpStatus.FORBIDDEN);
    }


    public boolean forgotPassword(ForgotPasswordDto forgotPasswordDto) {
        var email = forgotPasswordDto.getEmail();
        boolean exists = userRepository.existsByEmail(email);
        if (!exists) throw new HttpException(ErrorMessage.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        try {
            appProcessor.userDidForgetPassword(email);
        } catch (MessagingException e) {
            logger.error("Error while processing forgot password - Email[{}] - Message[{}]", email, e.getMessage());
        }
        return true;
    }


    public boolean resetPassword(ResetPasswordDto resetPasswordDto) {
        var validSignature = redisService.getValue("reset_password_signature:" + resetPasswordDto.getEmail());
        var signature = resetPasswordDto.getSignature();
        if (signature.equals(validSignature)) {
            var user = userRepository.findByEmail(resetPasswordDto.getEmail()).orElseThrow();
            user.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
            userService.save(user);
            redisService.deleteValue("refresh_token:" + user.getId());
            redisService.deleteValue("reset_password_signature:" + user.getEmail());
            return true;
        }
        throw new HttpException(ErrorMessage.INVALID_CREDENTIALS, HttpStatus.FORBIDDEN);
    }


    public Credentials refresh(RefreshDto refreshDto) {
        final String ERROR_MESSAGE = ErrorMessage.INVALID_CREDENTIALS;
        var token = refreshDto.getToken();
        var isValidToken = jwtService.isTokenValid(token, true);
        if (isValidToken) {
            String sub = jwtService.extractSub(token, true);
            String storedToken = redisService.getValue("refresh_token:" + sub);
            if (storedToken.equals(token)) {
                var user = userRepository
                        .findById(sub)
                        .orElseThrow(() -> new HttpException(ERROR_MESSAGE, HttpStatus.FORBIDDEN));
                return getCredentials(user);
            }
        }
        throw new HttpException(ERROR_MESSAGE, HttpStatus.FORBIDDEN);
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


    public boolean resendAccountVerification(ResendAccountVerificationDto resendAccountVerificationDto) {
        var user = userRepository.findByEmail(resendAccountVerificationDto.getEmail())
                .orElseThrow(() -> new HttpException(ErrorMessage.USER_NOT_FOUND, HttpStatus.BAD_REQUEST));
        try {
            appProcessor.userDidCreate(user);
        } catch (MessagingException e) {
            logger.error("Error while processing account verification - Email[{}] - Message[{}]", user.getEmail(), e.getMessage());
        }
        return true;
    }


    public AuthenticationResponse authenticateWithGoogle(GoogleAuthDto googleAuthDto) {
        WebClient webClient = WebClient
                .builder()
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.set("Authorization", "Bearer " + googleAuthDto.getAccessToken());
                })
                .baseUrl("https://www.googleapis.com")
                .build();

        var googleUserInfo = webClient
                .get().uri("/oauth2/v3/userinfo")
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> {
                            throw new HttpException(ErrorMessage.INVALID_CREDENTIALS, HttpStatus.FORBIDDEN);
                        })
                .bodyToMono(GoogleUserInfo.class)
                .block();

        if (googleUserInfo == null) throw new HttpException(ErrorMessage.INVALID_CREDENTIALS, HttpStatus.FORBIDDEN);

        var user = userRepository.findByEmail(googleUserInfo.getEmail()).orElse(null);
        boolean isNew = true;
        if (user != null) {
            user.setAvatarUrl(googleUserInfo.getPicture());
            user.setLastName(googleUserInfo.getFamilyName());
            user.setFirstName(googleUserInfo.getGivenName());
            user.setVerified(true);
            isNew = false;
        } else {
            user = User.builder()
                    .email(googleUserInfo.getEmail())
                    .avatarUrl(googleUserInfo.getPicture())
                    .role(Role.USER)
                    .firstName(googleUserInfo.getGivenName())
                    .lastName(googleUserInfo.getFamilyName())
                    .verified(true)
                    .password(Helper.generateRandomSecret(12))
                    .build();
        }

        userService.save(user);

        if (isNew) {
            try {
                appProcessor.userDidVerify(user);
            } catch (MessagingException e) {
                logger.error("Error while processing welcome user - Email[{}] - Message[{}]", user.getEmail(), e.getMessage());
            }
        }

        return AuthenticationResponse
                .builder()
                .credentials(getCredentials(user))
                .user(user)
                .build();
    }


    public boolean signOut(SignOutDto signOutDto, String userId) {
        var user = userRepository.findById(userId).orElseThrow(() -> new HttpException(ErrorMessage.USER_NOT_FOUND, HttpStatus.BAD_REQUEST));
        redisService.deleteValue("refresh_token:" + user.getId());
        pushNotificationService.removeToken(RemoveTokenDto
                .builder()
                .platform(signOutDto.getPlatform())
                .build(), userId, false);

        return true;
    }
}


@Data
@NoArgsConstructor
@AllArgsConstructor
class GoogleUserInfo {
    private String sub;
    private String name;
    @JsonProperty("given_name")
    private String givenName;
    @JsonProperty("family_name")
    private String familyName;
    private String picture;
    private String email;
    @JsonProperty("email_verified")
    private boolean emailVerified;
    private String locale;

    @Override
    public String toString() {
        return "GoogleUserInfo{" +
                "sub='" + sub + '\'' +
                ", name='" + name + '\'' +
                ", givenName='" + givenName + '\'' +
                ", familyName='" + familyName + '\'' +
                ", picture='" + picture + '\'' +
                ", email='" + email + '\'' +
                ", emailVerified=" + emailVerified +
                ", locale='" + locale + '\'' +
                '}';
    }
}