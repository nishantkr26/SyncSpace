package com.nishant.auth_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nishant.auth_service.dto.AuthRequest;
import com.nishant.auth_service.dto.AuthResponse;
import com.nishant.auth_service.dto.AuthRequest.LoginRequest;
import com.nishant.auth_service.dto.AuthRequest.UserRequest;
import com.nishant.auth_service.dto.AuthResponse.UserResponse;
import com.nishant.auth_service.services.UserService;
import com.nishant.auth_service.dto.AuthResponse.RegisterResponse;
import io.micrometer.core.ipc.http.HttpSender.Response;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/health")
    public String health() {
        return "Auth Service Running";
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRequest request) {
        try {
            String response = userService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        try {
            UserResponse response = userService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<RegisterResponse> getLoggedInUser(@RequestHeader("X-User-Email") String username) {
        // 1. Create your response DTO using the header data sent by the gateway
        RegisterResponse registerResponse = userService.getLoggedInUser(username);
        // 2. Return the matching object type
        return ResponseEntity.ok(registerResponse);
    }
}