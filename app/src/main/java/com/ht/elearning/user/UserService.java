package com.ht.elearning.user;

import com.ht.elearning.config.HttpException;
import com.ht.elearning.constants.ErrorMessage;
import com.ht.elearning.processor.AppProcessor;
import com.ht.elearning.processor.ElasticsearchSyncProcessor;
import com.ht.elearning.user.dtos.CreateUserDto;
import com.ht.elearning.user.dtos.UpdatePasswordDto;
import com.ht.elearning.user.dtos.UpdateProfileDto;
import com.ht.elearning.user.dtos.UpdateUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {
    private final AppProcessor appProcessor;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ElasticsearchSyncProcessor elasticsearchSyncProcessor;

    public User save(User user) {
        var savedUser = userRepository.save(user);
        elasticsearchSyncProcessor.userDidSave(savedUser);
        return savedUser;
    }

    public User findById(String userId) {
        return userRepository.findById(userId).orElseThrow(() -> new HttpException(ErrorMessage.USER_NOT_FOUND, HttpStatus.BAD_REQUEST));
    }

    public boolean deleteById(String userId) {
        var exists = userRepository.existsById(userId);
        if (!exists) throw new HttpException(ErrorMessage.USER_NOT_FOUND, HttpStatus.BAD_REQUEST);
        userRepository.deleteById(userId);
        return true;
    }

    public User create(CreateUserDto createUserDto) {
        try {
            var exists = userRepository.existsByEmail(createUserDto.getEmail());
            if (exists) throw new HttpException(ErrorMessage.USER_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
            var user = User.builder()
                    .lastName(createUserDto.getLastName())
                    .firstName(createUserDto.getFirstName())
                    .avatarUrl(createUserDto.getAvatarUrl())
                    .email(createUserDto.getEmail())
                    .password(passwordEncoder.encode(createUserDto.getPassword()))
                    .role(createUserDto.getRole())
                    .verified(createUserDto.isVerified())
                    .build();

            var savedUser = save(user);

            if (savedUser.isVerified()) {
                appProcessor.userDidVerify(savedUser);
            } else {
                appProcessor.userDidCreate(savedUser);
            }

            return savedUser;
        } catch (HttpException e) {
            throw new HttpException(e.getMessage(), e.getStatus());
        } catch (Exception e) {
            throw new HttpException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public User update(String userId, UpdateUserDto updateUserDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new HttpException(ErrorMessage.USER_NOT_FOUND, HttpStatus.BAD_REQUEST));

        Optional.ofNullable(updateUserDto.getAvatarUrl()).ifPresent(user::setAvatarUrl);
        Optional.ofNullable(updateUserDto.getPassword()).ifPresent(password -> user.setPassword(passwordEncoder.encode(password)));
        Optional.ofNullable(updateUserDto.getRole()).ifPresent(user::setRole);
        Optional.ofNullable(updateUserDto.getFirstName()).ifPresent(user::setFirstName);
        Optional.ofNullable(updateUserDto.getLastName()).ifPresent(user::setLastName);

        save(user);

        return user;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void syncToElasticsearch() {
        var users = userRepository.findAll();
        elasticsearchSyncProcessor.indexUsers(users);
    }

    public User updateProfile(UpdateProfileDto updateProfileDto, String userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new HttpException(ErrorMessage.USER_NOT_FOUND, HttpStatus.BAD_REQUEST));

        Optional.ofNullable(updateProfileDto.getAvatarUrl()).ifPresent(user::setAvatarUrl);
        Optional.ofNullable(updateProfileDto.getFirstName()).ifPresent(user::setFirstName);
        Optional.ofNullable(updateProfileDto.getLastName()).ifPresent(user::setLastName);

        save(user);

        return user;
    }

    public User updatePassword(UpdatePasswordDto updatePasswordDto, String userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new HttpException(ErrorMessage.USER_NOT_FOUND, HttpStatus.BAD_REQUEST));

        boolean isValid = passwordEncoder.matches(updatePasswordDto.getOldPassword(), user.getPassword());

        if (!isValid) throw new HttpException(ErrorMessage.INVALID_PASSWORD, HttpStatus.FORBIDDEN);

        user.setPassword(passwordEncoder.encode(updatePasswordDto.getNewPassword()));

        save(user);

        return user;
    }
}
