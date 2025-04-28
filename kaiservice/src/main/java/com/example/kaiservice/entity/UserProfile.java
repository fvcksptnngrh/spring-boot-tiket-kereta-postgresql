package com.example.kaiservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    @Id
    private Long id; // Menggunakan ID yang sama dengan User

    private String fullName;
    private String phoneNumber;
    private String address;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Menunjukkan bahwa ID diambil dari relasi User
    @JoinColumn(name = "id") // Nama kolom foreign key di tabel ini
    private User user;
}