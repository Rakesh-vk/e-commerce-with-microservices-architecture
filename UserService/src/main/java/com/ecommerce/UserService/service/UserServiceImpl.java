package com.ecommerce.UserService.service;

import com.ecommerce.UserService.dto.LoginRequest;
import com.ecommerce.UserService.dto.UserRegisterRequest;
import com.ecommerce.UserService.dto.UserResponse;
import com.ecommerce.UserService.entity.Role;
import com.ecommerce.UserService.entity.User;
import com.ecommerce.UserService.exception.BadCredentialsException;
import com.ecommerce.UserService.exception.DuplicateEmailException;
import com.ecommerce.UserService.mapper.UserMapper;
import com.ecommerce.UserService.repository.UserRepository;
import com.ecommerce.UserService.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

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
        user.setRole(Role.ROLE_USER);
        user.setPasswordHash(passwordEncoder.encode(userRegisterRequest.getPasswordHash()));

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
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        )) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(user);

        log.info("Login successful for user id: {}, email: {}", user.getId(), user.getEmail());
        return token;
    }
    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toResponse)
                .toList();
    }
}