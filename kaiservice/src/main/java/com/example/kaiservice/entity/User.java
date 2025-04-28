package com.example.kaiservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = { // Tambahkan unique constraints
    @UniqueConstraint(columnNames = "username"),
    @UniqueConstraint(columnNames = "email")
})
@Data // Lombok: generate getter, setter, toString, equals, hashCode
@NoArgsConstructor // Lombok: generate constructor tanpa argumen
@AllArgsConstructor // Lombok: generate constructor dengan semua argumen
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

    // Nanti bisa ditambahkan relasi ke role jika diperlukan
    // @ManyToMany(fetch = FetchType.LAZY)
    // @JoinTable(name = "user_roles", /* ... */)
    // private Set<Role> roles = new HashSet<>();

    // Relasi ke UserProfile (jika ada)
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private UserProfile userProfile;
}