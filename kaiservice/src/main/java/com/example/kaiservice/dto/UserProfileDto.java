package com.example.kaiservice.dto;

import lombok.Data;

@Data
public class UserProfileDto {
    private Long userId; // Mungkin perlu untuk referensi
    private String username; // Ambil dari User terkait
    private String email;    // Ambil dari User terkait
    private String fullName;
    private String phoneNumber;
    private String address;
}