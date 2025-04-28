package com.example.kaiservice.controller;

import com.example.kaiservice.dto.AuthResponse;
import com.example.kaiservice.dto.LoginRequest;
import com.example.kaiservice.dto.MessageResponse;
import com.example.kaiservice.dto.RegisterRequest;
import com.example.kaiservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // Import ResponseEntity
import org.springframework.web.bind.annotation.*; // Import anotasi web

@CrossOrigin(origins = "*", maxAge = 3600) // Izinkan request dari semua origin (sesuaikan di production)
@RestController // Menandakan ini adalah REST Controller
@RequestMapping("/api/auth") // Base path untuk controller ini
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse authResponse = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(authResponse); // Kirim token jika sukses
        } catch (Exception e) {
            // Tangani exception (misal: bad credentials)
            return ResponseEntity.status(401).body(new MessageResponse("Error: Invalid credentials!"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            authService.registerUser(registerRequest);
            return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
        } catch (RuntimeException e) {
            // Tangani exception (misal: username/email sudah ada)
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}