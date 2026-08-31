package com.ecommerce.UserService.service;

import com.ecommerce.UserService.dto.LoginRequest;
import com.ecommerce.UserService.dto.UserRegisterRequest;
import com.ecommerce.UserService.dto.UserResponse;
import com.ecommerce.UserService.entity.User;
import com.ecommerce.UserService.exception.BadCredentialsException;
import com.ecommerce.UserService.exception.DuplicateEmailException;
import com.ecommerce.UserService.mapper.UserMapper;
import com.ecommerce.UserService.security.JwtTokenProvider;

import com.ecommerce.UserService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;



    public UserResponse register(UserRegisterRequest userRegisterRequest){
        // checking if the email is already exist
        if(userRepository.existsByEmail(userRegisterRequest.getEmail())){
            throw new DuplicateEmailException(userRegisterRequest.getEmail());
        }
        User user= new User();
        user.setEmail(userRegisterRequest.getEmail());
        user.setUsername(userRegisterRequest.getUsername());
        user.setPasswordHash(userRegisterRequest.getPasswordHash());

        User saved = userRepository.save(user);
        return UserMapper.toResponse(saved);
    }

    @Override
    public String login(LoginRequest request) {
        User user= userRepository.findByEmail(request.getEmail()).
                orElseThrow();


        return jwtTokenProvider.generateToken(user);
    }
}
