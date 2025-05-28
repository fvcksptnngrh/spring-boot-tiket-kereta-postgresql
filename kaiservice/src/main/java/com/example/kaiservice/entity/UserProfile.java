package com.example.kaiservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    // Tidak ada @Id karena akan di-embed
    private String fullName;
    private String phoneNumber;
    private String address;

    // Tidak ada relasi @OneToOne User user; lagi di sini jika di-embed
}