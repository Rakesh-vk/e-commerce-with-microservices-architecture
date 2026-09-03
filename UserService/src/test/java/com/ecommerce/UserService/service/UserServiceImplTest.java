package com.ecommerce.UserService.service;

import com.ecommerce.UserService.dto.UserRegisterRequest;
import com.ecommerce.UserService.dto.UserResponse;
import com.ecommerce.UserService.entity.Role;
import com.ecommerce.UserService.entity.User;
import com.ecommerce.UserService.exception.DuplicateEmailException;
import com.ecommerce.UserService.repository.UserRepository;
import com.ecommerce.UserService.security.JwtTokenProvider;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void register_shouldSuccessfullyRegisterUser() {

        // Arrange
        UserRegisterRequest request = new UserRegisterRequest(
                "Rakesh",
                "rakesh@gmail.com",
                "password123"
        );

        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setUsername("Rakesh");
        savedUser.setEmail("rakesh@gmail.com");
        savedUser.setRole(Role.ROLE_USER);
        savedUser.setPasswordHash("encodedPassword");

        when(userRepository.existsByEmail("rakesh@gmail.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        // Act
        UserResponse response = userService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals("rakesh@gmail.com", response.getEmail());

        verify(userRepository).existsByEmail("rakesh@gmail.com");

        verify(passwordEncoder).encode("password123");

        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_fails_due_to_duplicate_email() {

        // Arrange
        UserRegisterRequest request = new UserRegisterRequest(
                "Rakesh",
                "rakesh@gmail.com",
                "password123"
        );

        when(userRepository.existsByEmail("rakesh@gmail.com"))
                .thenReturn(true);

        // Act + Assert
        assertThrows(
                DuplicateEmailException.class,
                () -> userService.register(request)
        );

        // Verify
        verify(userRepository)
                .existsByEmail("rakesh@gmail.com");

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userRepository, never())
                .save(any(User.class));
    }
}