// src/main/java/com/example/kaiservice/service/AuthService.java
package com.example.kaiservice.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager; // Import entity Role
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication; // Import RoleRepository
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kaiservice.dto.AuthResponse;
import com.example.kaiservice.dto.LoginRequest;
import com.example.kaiservice.dto.RegisterRequest;
import com.example.kaiservice.entity.Role;
import com.example.kaiservice.entity.User;
import com.example.kaiservice.repository.RoleRepository; // Import Transactional
import com.example.kaiservice.repository.UserRepository; // Import HashSet
import com.example.kaiservice.security.JwtUtils;     // Import Set

@Service
public class AuthService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository; // Inject RoleRepository

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Transactional // Tambahkan Transactional untuk operasi yang melibatkan beberapa save
    public void registerUser(RegisterRequest registerRequest) {
        // Cek apakah username atau email sudah ada
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Error: Username '" + registerRequest.getUsername() + "' is already taken!");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Error: Email '" + registerRequest.getEmail() + "' is already in use!");
        }

        // Buat user baru
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encoder.encode(registerRequest.getPassword())); // Hash password!

        // Tetapkan role default untuk user baru (misalnya ROLE_USER)
        Set<Role> roles = new HashSet<>();
        // Pastikan nama "ROLE_USER" sesuai dengan apa yang ada di tabel 'roles' Anda
        // dan sudah di-insert oleh data.sql (misalnya dengan id=2)
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Default Role (ROLE_USER) not found in database. Please ensure it is seeded."));
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);
    }

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // Ambil user dari database untuk mendapatkan ID dan informasi lainnya jika perlu
        User userFromDb = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found after authentication with username: " + userDetails.getUsername()));

        return new AuthResponse(jwt, userFromDb.getId(), userFromDb.getUsername());
    }
}