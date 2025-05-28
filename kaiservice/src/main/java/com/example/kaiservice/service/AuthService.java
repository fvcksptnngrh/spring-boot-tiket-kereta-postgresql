package com.example.kaiservice.service;

import com.example.kaiservice.dto.AuthResponse;
import com.example.kaiservice.dto.LoginRequest;
import com.example.kaiservice.dto.RegisterRequest;
import com.example.kaiservice.entity.Role;
import com.example.kaiservice.entity.User;
import com.example.kaiservice.repository.RoleRepository;
import com.example.kaiservice.repository.UserRepository;
import com.example.kaiservice.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Tetap bisa digunakan

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository; // Sekarang MongoRepository<User, String>

    @Autowired
    RoleRepository roleRepository; // Sekarang MongoRepository<Role, String>

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Transactional
    public void registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Error: Username '" + registerRequest.getUsername() + "' is already taken!");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Error: Email '" + registerRequest.getEmail() + "' is already in use!");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encoder.encode(registerRequest.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Default Role (ROLE_USER) not found in database."));
        roles.add(userRole);
        user.setRoles(roles);
        // Jika UserProfile di-embed dan Anda ingin menginisialisasi beberapa field default:
        // user.setUserProfile(new UserProfile("Default Name", null, null));

        userRepository.save(user);
    }

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User userFromDb = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found after authentication with username: " + userDetails.getUsername()));

        // ID pengguna sekarang String
        return new AuthResponse(jwt, userFromDb.getId(), userFromDb.getUsername());
    }
}