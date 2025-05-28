package com.example.kaiservice.dto;

import lombok.Data;

@Data
public class UserProfileDto {
    private String userId; // Diubah dari Long ke String
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
}