package com.ecommerce.UserService.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserRegisterRequest {
    @NotBlank
    private String username;
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    @NotBlank(message = "Password is required")
    private String passwordHash;
    public UserRegisterRequest(String _username,String _email,String _passwordHash){
        this.username=_username;
        this.email=_email;
        this.passwordHash=_passwordHash;
    }
}
