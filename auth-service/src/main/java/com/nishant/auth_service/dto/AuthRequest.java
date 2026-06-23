package com.nishant.auth_service.dto;

public class AuthRequest {
    public record UserRequest(
            String name,
            String email,
            String password) {
    }


    public record LoginRequest (String email,String password){

    }
}