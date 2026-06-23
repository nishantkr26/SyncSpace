package com.nishant.auth_service.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.nishant.auth_service.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import com.nishant.auth_service.utility.JWTUtil;
import com.nishant.auth_service.dto.AuthRequest.LoginRequest;
import com.nishant.auth_service.dto.AuthRequest.UserRequest;
import com.nishant.auth_service.dto.AuthResponse.UserResponse;
import com.nishant.auth_service.entity.User;
import com.nishant.auth_service.config.SecurityConfig;
import com.nishant.auth_service.dto.AuthResponse;
import com.nishant.auth_service.enums.Role;
import com.nishant.auth_service.dto.AuthResponse.RegisterResponse;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public String register(UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already registered with us");
        }

        String hashedPassword = passwordEncoder.encode(request.password());

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(hashedPassword)
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return "User registered successfully";
    }

    public UserResponse login(LoginRequest request) {
        // This converts the Optional<User> into a straight User object, or throws an
        // error if empty

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Email not registered with us, Kindly register first"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Wrong password, please try with a different password");
        } else {
            String token = jwtUtil.generateToken(user.getEmail());
            return new UserResponse(token);
        }
    }


    public RegisterResponse getLoggedInUser(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        return new RegisterResponse(user.getEmail(), user.getName(), user.getRole().name(), user.getId().toString());
    }

}
