package com.skt.service;

import com.skt.repository.AuthUserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthUserRepository userRepository;

    public AuthService(AuthUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean authenticate(String username, String password) {

        return userRepository.findByUsername(username)
                .map(user -> user.getPassword().equals(password))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}