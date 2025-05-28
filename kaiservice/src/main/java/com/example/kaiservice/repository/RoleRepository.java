package com.example.kaiservice.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.kaiservice.entity.Role;

@Repository
public interface RoleRepository extends MongoRepository<Role, String> { // Menggunakan String untuk ID
    Optional<Role> findByName(String name);
}