package com.ht.elearning.auth;

import com.ht.elearning.auth.dtos.AuthenticateDto;
import com.ht.elearning.auth.dtos.RegisterDto;
import com.ht.elearning.auth.dtos.VerifyAccountDto;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        when(userRepository.existsByEmail(registerDto.getEmail())).thenReturn(false);

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
    public void whenVerifyEmailWithExistsEmailAndUserIsNotVerifiedAndSignatureIsValid_shouldVerify() {
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
        verify(userService).save(any(User.class));
    }
}