package com.example.kaiservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "username"),
    @UniqueConstraint(columnNames = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // Password akan disimpan dalam bentuk hash

    // --- BAGIAN YANG DIPERBAIKI DAN DIAKTIFKAN ---
    @ManyToMany(fetch = FetchType.EAGER) // Menggunakan EAGER agar peran langsung dimuat
    @JoinTable(name = "user_roles", // Nama tabel penghubung yang sudah Anda buat
               joinColumns = @JoinColumn(name = "user_id"), // Kolom di user_roles yang merujuk ke tabel users (id pengguna)
               inverseJoinColumns = @JoinColumn(name = "role_id")) // Kolom di user_roles yang merujuk ke tabel roles (id peran)
    private Set<Role> roles = new HashSet<>(); // Pastikan diinisialisasi
    // --- SELESAI BAGIAN YANG DIPERBAIKI ---

    // Relasi ke UserProfile (jika ada) - ini sudah benar jika Anda memang menggunakannya
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private UserProfile userProfile;
}