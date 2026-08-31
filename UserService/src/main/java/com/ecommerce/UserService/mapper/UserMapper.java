package com.ecommerce.UserService.mapper;

import com.ecommerce.UserService.dto.UserResponse;
import com.ecommerce.UserService.entity.User;

public class UserMapper {
    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getCreatedAt()
        );
    }
}