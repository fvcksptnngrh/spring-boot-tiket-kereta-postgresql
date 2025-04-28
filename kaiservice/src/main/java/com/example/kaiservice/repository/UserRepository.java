package com.example.kaiservice.repository;

import com.example.kaiservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository // Menandakan ini adalah Spring Data repository
public interface UserRepository extends JpaRepository<User, Long> { // Entity: User, Tipe ID: Long

    // Spring Data JPA otomatis membuat query berdasarkan nama method
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Boolean existsByUsername(String username); // Cek apakah username sudah ada

    Boolean existsByEmail(String email); // Cek apakah email sudah ada
}