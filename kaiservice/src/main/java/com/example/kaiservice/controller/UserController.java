package com.example.kaiservice.controller;

import com.example.kaiservice.dto.MessageResponse;
import com.example.kaiservice.dto.UserProfileDto;
import com.example.kaiservice.dto.UserDto; // Misalkan Anda punya DTO untuk daftar user
import com.example.kaiservice.entity.User; // Atau langsung entitas jika sederhana
import com.example.kaiservice.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Penting untuk keamanan level method
import org.springframework.web.bind.annotation.*;

import java.util.List; // Untuk daftar pengguna
import java.util.stream.Collectors; // Jika perlu mapping

@RestController
@RequestMapping("/api/users") // Base path untuk endpoint user
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    // --- TAMBAHKAN METHOD INI ---
    @GetMapping // Ini akan menangani GET /api/users
    @PreAuthorize("hasRole('ADMIN')") // Keamanan tambahan di level method, pastikan @EnableMethodSecurity aktif
    public ResponseEntity<List<UserDto>> getAllUsers() { // Atau List<User> jika sederhana
        logger.info(">>> UserController: getAllUsers method entered by ADMIN.");
        List<User> users = userService.findAllUsers(); // Misalkan ada method ini di service Anda
        // Ubah ke DTO jika perlu untuk menghindari ekspos data sensitif seperti password
        List<UserDto> userDtos = users.stream()
                                     .map(user -> new UserDto(user.getId(), user.getUsername(), user.getEmail())) // Contoh DTO
                                     .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }
    // --- SELESAI PENAMBAHAN METHOD ---

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()") // Anda bisa aktifkan ini jika perlu: .hasAnyRole('USER', 'ADMIN') atau isAuthenticated()
    public ResponseEntity<UserProfileDto> getUserProfile() {
        try {
            UserProfileDto profileDto = userService.getUserProfile();
            return ResponseEntity.ok(profileDto);
        } catch (RuntimeException e) {
            logger.error(">>> UserController: Error getting user profile", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateUserProfile(@RequestBody UserProfileDto profileUpdateRequest) {
        logger.info(">>> UserController: updateUserProfile method entered.");
        try {
            UserProfileDto updatedProfile = userService.updateUserProfile(profileUpdateRequest);
            logger.info(">>> UserController: updateUserProfile successful for user: {}", updatedProfile.getUsername());
            return ResponseEntity.ok(updatedProfile);
        } catch (RuntimeException e) {
            logger.error(">>> UserController: Error updating profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(new MessageResponse("Error occurred while updating profile: " + e.getMessage()));
        }
    }
}