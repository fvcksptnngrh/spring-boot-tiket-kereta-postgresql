package com.example.kaiservice.service;

import com.example.kaiservice.dto.AuthResponse;
import com.example.kaiservice.dto.LoginRequest;
import com.example.kaiservice.dto.RegisterRequest;
import com.example.kaiservice.entity.User;
import com.example.kaiservice.repository.UserRepository;
import com.example.kaiservice.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails; // Import UserDetails
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    AuthenticationManager authenticationManager; // Untuk proses login

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder; // Untuk hash password

    @Autowired
    JwtUtils jwtUtils; // Untuk generate token

    public void registerUser(RegisterRequest registerRequest) {
        // Cek apakah username atau email sudah ada
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!"); // Ganti dengan exception yg lebih spesifik
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!"); // Ganti dengan exception yg lebih spesifik
        }

        // Buat user baru
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encoder.encode(registerRequest.getPassword())); // Hash password!

        userRepository.save(user);
    }

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        // Otentikasi menggunakan AuthenticationManager Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // Jika otentikasi berhasil, set authentication di SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate token JWT
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Dapatkan detail user dari object Authentication
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
            () -> new RuntimeException("User not found after authentication") // Seharusnya tidak terjadi
        );

        // Kembalikan response berisi token dan info user
        return new AuthResponse(jwt, user.getId(), user.getUsername());
    }
}