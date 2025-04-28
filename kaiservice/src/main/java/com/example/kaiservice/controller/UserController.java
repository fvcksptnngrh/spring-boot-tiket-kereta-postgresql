package com.example.kaiservice.controller;

import com.example.kaiservice.dto.MessageResponse; // <-- Tambahkan import ini jika belum ada
import com.example.kaiservice.dto.UserProfileDto;
import com.example.kaiservice.service.UserService;

// Import untuk Logging (pilih salah satu atau sesuaikan)
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // <-- Tambahkan import ini
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize; // Jika ingin pakai anotasi method security
import org.springframework.web.bind.annotation.*;
// import jakarta.validation.Valid; // Uncomment jika Anda implementasi validasi

@RestController
@RequestMapping("/api/users") // Base path untuk endpoint user
public class UserController {

    // Tambahkan logger instance
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    // Endpoint untuk mendapatkan profil pengguna yang sedang login
    @GetMapping("/profile")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDto> getUserProfile() {
        // Tambahkan log jika perlu
        // logger.info(">>> UserController: getUserProfile method entered.");
        try {
            UserProfileDto profileDto = userService.getUserProfile();
            return ResponseEntity.ok(profileDto);
        } catch (RuntimeException e) {
            // Tangani jika user tidak ditemukan (misal karena token aneh, meski jarang)
            logger.error(">>> UserController: Error getting user profile", e); // Log errornya
            // Sebaiknya kembalikan 404 atau 500 tergantung kasus, 404 cukup baik jika user tidak ada
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(null); // Atau body(new MessageResponse("User profile not found"))
        }
    }

    // Endpoint untuk mengupdate (atau membuat) profil pengguna yang sedang login
    @PostMapping("/profile")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateUserProfile(/*@Valid*/ @RequestBody UserProfileDto profileUpdateRequest) { // Tambahkan @Valid jika pakai validasi
        // Tambahkan log
        logger.info(">>> UserController: updateUserProfile method entered.");
        try {
            UserProfileDto updatedProfile = userService.updateUserProfile(profileUpdateRequest);
            logger.info(">>> UserController: updateUserProfile successful for user: {}", updatedProfile.getUsername());
            return ResponseEntity.ok(updatedProfile);
        } catch (RuntimeException e) {
            // --- BAGIAN YANG DIPERBAIKI ---
            // Log error yang sebenarnya terjadi di service
            logger.error(">>> UserController: Error updating profile", e);

            // Kembalikan 500 Internal Server Error, BUKAN 401 Unauthorized
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(new MessageResponse("Error occurred while updating profile: " + e.getMessage()));
            // Anda bisa lebih spesifik jika menangkap exception tertentu dari service, misal:
            // } catch (ResourceNotFoundException e) {
            //    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
            // } catch (DataIntegrityViolationException e) { // Contoh jika ada constraint DB
            //    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Data conflict occurred."));
            // } catch (Exception e) { // Tangkapan umum terakhir
            //    logger.error(">>> UserController: Unexpected error updating profile", e);
            //    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("An unexpected error occurred."));
            // }
            // --- AKHIR BAGIAN YANG DIPERBAIKI ---
        }
    }
}