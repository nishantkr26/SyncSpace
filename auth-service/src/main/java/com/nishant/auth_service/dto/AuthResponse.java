package com.nishant.auth_service.dto;

public class AuthResponse {
    public record UserResponse (String accessToken){

    }

    public record RegisterResponse (String email,String name,String role,String userId){

    }
}