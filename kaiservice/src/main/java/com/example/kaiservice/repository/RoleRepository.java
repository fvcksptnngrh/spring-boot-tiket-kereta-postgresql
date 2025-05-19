// src/main/java/com/example/kaiservice/repository/RoleRepository.java
package com.example.kaiservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // Opsional, tapi baik untuk konsistensi

import com.example.kaiservice.entity.Role;

@Repository // Opsional, tapi baik untuk konsistensi
public interface RoleRepository extends JpaRepository<Role, Integer> { // Tipe ID Role adalah Integer
    Optional<Role> findByName(String name); // Method untuk mencari Role berdasarkan namanya
}