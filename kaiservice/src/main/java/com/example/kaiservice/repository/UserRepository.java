package com.example.kaiservice.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.kaiservice.entity.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> { // Menggunakan String untuk ID
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email); //
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}