package com.ecommerce.UserService.service;

import com.ecommerce.UserService.dto.LoginRequest;
import com.ecommerce.UserService.dto.UserRegisterRequest;
import com.ecommerce.UserService.dto.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse register(UserRegisterRequest request);
    String login(LoginRequest request); // returns JWT access token
    List<UserResponse> getAllUsers();
}
