package com.example.kaiservice.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger; // UserDto sekarang memiliki ID String
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController; // Jika UserProfileDto memiliki validasi

import com.example.kaiservice.dto.MessageResponse;
import com.example.kaiservice.dto.UserDto;
import com.example.kaiservice.dto.UserProfileDto;
import com.example.kaiservice.entity.User;
import com.example.kaiservice.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        logger.info(">>> UserController: getAllUsers method entered by ADMIN.");
        List<User> users = userService.findAllUsers();
        List<UserDto> userDtos = users.stream()
                                     // UserDto sekarang memiliki constructor dengan ID String
                                     .map(user -> new UserDto(user.getId(), user.getUsername(), user.getEmail()))
                                     .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDto> getUserProfile() {
        try {
            UserProfileDto profileDto = userService.getUserProfile(); // UserProfileDto memiliki userId String
            return ResponseEntity.ok(profileDto);
        } catch (RuntimeException e) {
            logger.error(">>> UserController: Error getting user profile", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateUserProfile(@Valid @RequestBody UserProfileDto profileUpdateRequest) { // UserProfileDto memiliki userId String
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
    
    // Jika Anda menambahkan endpoint seperti /api/users/{userId}
    // @GetMapping("/{userId}")
    // @PreAuthorize("hasRole('ADMIN')")
    // public ResponseEntity<UserDto> getUserById(@PathVariable String userId) { // userId menjadi String
    //     // ... implementasi untuk mengambil user by ID String ...
    // }
}