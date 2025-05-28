package com.example.kaiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private String userId; 
    private String username;

    public AuthResponse(String token, String userId, String username) { 
        this.token = token;
        this.userId = userId;
        this.username = username;
    }
}