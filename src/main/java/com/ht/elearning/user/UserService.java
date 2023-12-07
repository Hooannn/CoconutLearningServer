package com.ht.elearning.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    public Optional<User> getAuthenticatedUser(String sub) {
        return repository.findById(sub);
    }
}
