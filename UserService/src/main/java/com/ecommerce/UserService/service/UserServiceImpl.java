package com.ecommerce.UserService.service;

import com.ecommerce.UserService.dto.LoginRequest;
import com.ecommerce.UserService.dto.UserRegisterRequest;
import com.ecommerce.UserService.dto.UserResponse;
import com.ecommerce.UserService.entity.User;
import com.ecommerce.UserService.exception.BadCredentialsException;
import com.ecommerce.UserService.exception.DuplicateEmailException;
import com.ecommerce.UserService.mapper.UserMapper;
import com.ecommerce.UserService.repository.UserRepository;
import com.ecommerce.UserService.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public UserResponse register(UserRegisterRequest userRegisterRequest) {
        // checking if the email is already exist
        log.info("Registering user with email: {}", userRegisterRequest.getEmail());

        if (userRepository.existsByEmail(userRegisterRequest.getEmail())) {
            log.warn("Registration failed. Email already exists: {}", userRegisterRequest.getEmail());
            throw new DuplicateEmailException(userRegisterRequest.getEmail());
        }

        User user = new User();
        user.setEmail(userRegisterRequest.getEmail());
        user.setUsername(userRegisterRequest.getUsername());
        user.setPasswordHash(userRegisterRequest.getPasswordHash());

        User saved = userRepository.save(user);

        log.info("User registered successfully with id: {}, email: {}", saved.getId(), saved.getEmail());
        return UserMapper.toResponse(saved);
    }

    @Override
    public String login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed. User not found with email: {}", request.getEmail());
                    return new BadCredentialsException("Invalid email or password");
                });

        String token = jwtTokenProvider.generateToken(user);

        log.info("Login successful for user id: {}, email: {}", user.getId(), user.getEmail());
        return token;
    }
}