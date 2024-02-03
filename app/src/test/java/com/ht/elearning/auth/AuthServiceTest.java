package com.ht.elearning.auth;

import com.ht.elearning.auth.dtos.*;
import com.ht.elearning.config.HttpException;
import com.ht.elearning.constants.ErrorMessage;
import com.ht.elearning.jwt.JwtService;
import com.ht.elearning.processor.AppProcessor;
import com.ht.elearning.redis.RedisService;
import com.ht.elearning.user.Role;
import com.ht.elearning.user.User;
import com.ht.elearning.user.UserRepository;
import com.ht.elearning.user.UserService;
import com.ht.elearning.utils.Helper;
import com.ht.elearning.utils.MD5;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @InjectMocks
    private AuthService underTest;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserService userService;
    @Mock
    private RedisService redisService;
    @Mock
    private AppProcessor appProcessor;
    @Mock
    private JwtService jwtService;

    @BeforeEach
    public void setUp() {

    }

    @Test
    public void whenRegisterWithExistsEmail_shouldThrowHttpException() {
        RegisterDto registerDto = RegisterDto.builder()
                .email("khaihoan.9a9@gmail.com").firstName("John").lastName("Doe").password("12345678")
                .build();

        when(userRepository.existsByEmail(registerDto.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> underTest.register(registerDto))
                .isInstanceOf(HttpException.class)
                .hasMessage(ErrorMessage.USER_ALREADY_EXISTS);

        verify(userRepository).existsByEmail(registerDto.getEmail());
    }

    @Test
    public void whenRegisterWithNotExistsEmail_shouldRegister() throws MessagingException {
        RegisterDto registerDto = RegisterDto.builder()
                .email("khaihoan.9a9@gmail.com").firstName("John").lastName("Doe").password("12345678")
                .build();
        String hashedPassword = Helper.generateRandomSecret(64);
        String hash = MD5.md5Hex(registerDto.getEmail().toLowerCase());
        String defaultAvatarUrl = "https://gravatar.com/avatar/" + hash;
        User shouldCreateUser = User.builder()
                .id(UUID.randomUUID().toString())
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .email(registerDto.getEmail())
                .avatarUrl(defaultAvatarUrl)
                .enabledEmailNotification(true)
                .enabledPushNotification(true)
                .password(hashedPassword)
                .role(Role.USER)
                .build();

        when(userRepository.existsByEmail(registerDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerDto.getPassword())).thenReturn(hashedPassword);
        when(userService.save(any(User.class))).thenReturn(shouldCreateUser);

        AuthenticationResponse authenticationResponse = underTest.register(registerDto);

        assertThat(authenticationResponse.getUser().getEmail()).isEqualTo(registerDto.getEmail());
        assertThat(authenticationResponse.getUser().getFirstName()).isEqualTo(registerDto.getFirstName());
        assertThat(authenticationResponse.getUser().getLastName()).isEqualTo(registerDto.getLastName());
        assertThat(authenticationResponse.getUser().getAvatarUrl()).isEqualTo(defaultAvatarUrl);
        assertThat(authenticationResponse.getUser().getPassword()).isEqualTo(hashedPassword);
        assertThat(authenticationResponse.getUser().getRole()).isEqualTo(Role.USER);
        assertThat(authenticationResponse.getUser().isEnabledPushNotification()).isEqualTo(true);
        assertThat(authenticationResponse.getUser().isEnabledEmailNotification()).isEqualTo(true);
        assertThat(authenticationResponse.getUser().isVerified()).isEqualTo(false);

        verify(userRepository).existsByEmail(registerDto.getEmail());
        verify(appProcessor).userDidCreate(shouldCreateUser);
        verify(userService).save(any(User.class));
    }


    @Test
    public void whenVerifyEmailWithNotExistsEmail_shouldThrowHttpException() {
        VerifyAccountDto verifyAccountDto = VerifyAccountDto.builder()
                .email("notExist@gmail.com").signature(UUID.randomUUID().toString()).build();

        when(userRepository.findByEmail(verifyAccountDto.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.verifyAccount(verifyAccountDto))
                .isInstanceOf(HttpException.class)
                .hasMessage(ErrorMessage.INVALID_CREDENTIALS);

        verify(userRepository).findByEmail(verifyAccountDto.getEmail());
    }

    @Test
    public void whenVerifyEmailWithExistsEmailAndUserIsVerified_shouldThrowHttpException() {
        VerifyAccountDto verifyAccountDto = VerifyAccountDto.builder()
                .email("exist@gmail.com").signature(UUID.randomUUID().toString()).build();

        User shouldReturnUser = User.builder().id(UUID.randomUUID().toString()).email(verifyAccountDto.getEmail()).verified(true).build();

        when(userRepository.findByEmail(verifyAccountDto.getEmail())).thenReturn(Optional.of(shouldReturnUser));

        assertThatThrownBy(() -> underTest.verifyAccount(verifyAccountDto))
                .isInstanceOf(HttpException.class)
                .hasMessage(ErrorMessage.REQUEST_NOT_ACCEPTABLE);

        verify(userRepository).findByEmail(verifyAccountDto.getEmail());
    }

    @Test
    public void whenVerifyEmailWithExistsEmailAndUserIsNotVerifiedAndSignatureIsNotValid_shouldThrowHttpException() {
        VerifyAccountDto verifyAccountDto = VerifyAccountDto.builder()
                .email("exist@gmail.com").signature(UUID.randomUUID().toString()).build();

        User shouldReturnUser = User.builder().id(UUID.randomUUID().toString()).email(verifyAccountDto.getEmail()).verified(false).build();

        when(userRepository.findByEmail(verifyAccountDto.getEmail())).thenReturn(Optional.of(shouldReturnUser));
        when(redisService.getValue("account_signature:" + verifyAccountDto.getEmail())).thenReturn(UUID.randomUUID().toString());

        assertThatThrownBy(() -> underTest.verifyAccount(verifyAccountDto))
                .isInstanceOf(HttpException.class)
                .hasMessage(ErrorMessage.INVALID_CREDENTIALS);

        verify(userRepository).findByEmail(verifyAccountDto.getEmail());
        verify(redisService).getValue("account_signature:" + verifyAccountDto.getEmail());
    }

    @Test
    public void whenVerifyEmailWithExistsEmailAndUserIsNotVerifiedAndSignatureIsValid_shouldSuccess() throws MessagingException {
        String signature = UUID.randomUUID().toString();
        VerifyAccountDto verifyAccountDto = VerifyAccountDto.builder()
                .email("exist@gmail.com").signature(signature).build();

        User shouldReturnUser = User.builder().id(UUID.randomUUID().toString()).email(verifyAccountDto.getEmail()).verified(false).build();

        when(userRepository.findByEmail(verifyAccountDto.getEmail())).thenReturn(Optional.of(shouldReturnUser));
        when(redisService.getValue("account_signature:" + verifyAccountDto.getEmail())).thenReturn(signature);
        when(userService.save(any(User.class))).thenReturn(shouldReturnUser);

        AuthenticationResponse authenticationResponse = underTest.verifyAccount(verifyAccountDto);
        assertThat(authenticationResponse.getUser().isVerified()).isEqualTo(true);

        verify(userRepository).findByEmail(verifyAccountDto.getEmail());
        verify(redisService).getValue("account_signature:" + verifyAccountDto.getEmail());
        verify(appProcessor).userDidVerify(shouldReturnUser);
        verify(userService).save(any(User.class));
    }

    @Test
    public void whenForgotPasswordWithNotExistsEmail_shouldThrowHttpException() {
        ForgotPasswordDto forgotPasswordDto = ForgotPasswordDto.builder().email("notExist@gmail.com").build();

        when(userRepository.existsByEmail(forgotPasswordDto.getEmail())).thenReturn(false);

        assertThatThrownBy(() -> underTest.forgotPassword(forgotPasswordDto))
                .isInstanceOf(HttpException.class)
                .hasMessage(ErrorMessage.BAD_REQUEST);

        verify(userRepository).existsByEmail(forgotPasswordDto.getEmail());
    }

    @Test
    public void whenForgotPasswordWithExistsEmail_shouldSuccess() throws MessagingException {
        ForgotPasswordDto forgotPasswordDto = ForgotPasswordDto.builder().email("notExist@gmail.com").build();

        when(userRepository.existsByEmail(forgotPasswordDto.getEmail())).thenReturn(true);

        boolean result = underTest.forgotPassword(forgotPasswordDto);

        assertThat(result).isEqualTo(true);

        verify(appProcessor).userDidForgetPassword(forgotPasswordDto.getEmail());
        verify(userRepository).existsByEmail(forgotPasswordDto.getEmail());
    }

    @Test
    public void whenResetPasswordWithInvalidSignature_shouldThrowHttpException() {
        ResetPasswordDto resetPasswordDto = ResetPasswordDto.builder()
                .email("exist@gmail.com").newPassword("12345678").signature(UUID.randomUUID().toString()).build();

        when(redisService.getValue("reset_password_signature:" + resetPasswordDto.getEmail())).thenReturn(UUID.randomUUID().toString());

        assertThatThrownBy(() -> underTest.resetPassword(resetPasswordDto))
                .isInstanceOf(HttpException.class)
                .hasMessage(ErrorMessage.INVALID_CREDENTIALS);

        verify(redisService).getValue("reset_password_signature:" + resetPasswordDto.getEmail());
    }

    @Test
    public void whenResetPasswordWithValidSignature_shouldSuccess() {
        String signature = UUID.randomUUID().toString();
        ResetPasswordDto resetPasswordDto = ResetPasswordDto.builder()
                .email("exist@gmail.com").newPassword("12345678").signature(signature).build();
        User shouldReturnUser = User.builder().id(UUID.randomUUID().toString()).email(resetPasswordDto.getEmail()).build();

        when(redisService.getValue("reset_password_signature:" + resetPasswordDto.getEmail())).thenReturn(signature);
        when(userRepository.findByEmail(resetPasswordDto.getEmail())).thenReturn(Optional.of(shouldReturnUser));
        when(userService.save(any(User.class))).thenReturn(shouldReturnUser);
        when(passwordEncoder.encode(resetPasswordDto.getNewPassword())).thenReturn(Helper.generateRandomSecret(64));

        boolean result = underTest.resetPassword(resetPasswordDto);

        assertThat(result).isEqualTo(true);

        verify(redisService).getValue("reset_password_signature:" + resetPasswordDto.getEmail());
        verify(userRepository).findByEmail(resetPasswordDto.getEmail());
        verify(userService).save(any(User.class));
        verify(redisService).deleteValue("refresh_token:" + shouldReturnUser.getId());
        verify(redisService).deleteValue("reset_password_signature:" + shouldReturnUser.getEmail());

    }

    @Test
    public void whenRefreshTokensWithInvalidJwt_shouldThrowHttpException() {
        RefreshDto refreshTokenDto = RefreshDto.builder().token(Helper.generateRandomSecret(64)).build();

        when(jwtService.isTokenValid(refreshTokenDto.getToken(), true)).thenReturn(false);

        assertThatThrownBy(() -> underTest.refresh(refreshTokenDto))
                .isInstanceOf(HttpException.class)
                .hasMessage(ErrorMessage.INVALID_CREDENTIALS);

        verify(jwtService).isTokenValid(refreshTokenDto.getToken(), true);
    }

    @Test
    public void whenRefreshTokensWithValidJwtButNotEqualWithServerStorage_shouldThrowHttpException() {
        String token = Helper.generateRandomSecret(64);
        String storedToken = Helper.generateRandomSecret(64);
        String sub = UUID.randomUUID().toString();
        RefreshDto refreshTokenDto = RefreshDto.builder().token(token).build();

        when(jwtService.isTokenValid(refreshTokenDto.getToken(), true)).thenReturn(true);
        when(jwtService.extractSub(refreshTokenDto.getToken(), true)).thenReturn(sub);
        when(redisService.getValue("refresh_token:" + sub)).thenReturn(storedToken);

        assertThatThrownBy(() -> underTest.refresh(refreshTokenDto))
                .isInstanceOf(HttpException.class)
                .hasMessage(ErrorMessage.INVALID_CREDENTIALS);

        verify(jwtService).isTokenValid(refreshTokenDto.getToken(), true);
        verify(jwtService).extractSub(refreshTokenDto.getToken(), true);
        verify(redisService).getValue("refresh_token:" + sub);
    }

    @Test
    public void whenRefreshTokensWithValidJwtAndEqualWithServerStorage_shouldSuccess() {
        String token = Helper.generateRandomSecret(64);
        String storedToken = token;
        String sub = UUID.randomUUID().toString();
        String accessToken = Helper.generateRandomSecret(64);
        String refreshToken = Helper.generateRandomSecret(64);
        RefreshDto refreshTokenDto = RefreshDto.builder().token(token).build();
        User shouldReturnUser = User.builder().id(sub).build();

        when(jwtService.isTokenValid(refreshTokenDto.getToken(), true)).thenReturn(true);
        when(jwtService.extractSub(refreshTokenDto.getToken(), true)).thenReturn(sub);
        when(redisService.getValue("refresh_token:" + sub)).thenReturn(storedToken);
        when(userRepository.findById(sub)).thenReturn(Optional.of(shouldReturnUser));
        when(jwtService.generateAccessToken(shouldReturnUser)).thenReturn(accessToken);
        when(jwtService.generateRefreshToken(shouldReturnUser)).thenReturn(refreshToken);

        Credentials credentials = underTest.refresh(refreshTokenDto);

        assertThat(credentials.getAccessToken()).isEqualTo(accessToken);
        assertThat(credentials.getRefreshToken()).isEqualTo(refreshToken);

        verify(jwtService).isTokenValid(refreshTokenDto.getToken(), true);
        verify(jwtService).extractSub(refreshTokenDto.getToken(), true);
        verify(redisService).getValue("refresh_token:" + shouldReturnUser.getId());
        verify(userRepository).findById(sub);
        verify(jwtService).generateAccessToken(shouldReturnUser);
        verify(jwtService).generateRefreshToken(shouldReturnUser);
    }
}