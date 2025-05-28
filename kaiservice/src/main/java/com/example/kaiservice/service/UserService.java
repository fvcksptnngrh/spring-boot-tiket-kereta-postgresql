package com.example.kaiservice.service;

import com.example.kaiservice.dto.UserProfileDto;
import com.example.kaiservice.entity.User;
import com.example.kaiservice.entity.UserProfile; // Kelas UserProfile (untuk embedded)
import com.example.kaiservice.repository.UserRepository; // MongoRepository<User, String>
// UserProfileRepository tidak di-inject lagi

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
// Optional tidak diperlukan untuk findByUser_Id karena UserProfile di-embed

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    // UserProfileRepository tidak lagi di-autowired

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        logger.info("Fetching all users from database.");
        return userRepository.findAll();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            logger.warn("Attempted to get current user but no authentication found or user is anonymous.");
            throw new RuntimeException("No authenticated user found. Please login.");
        }
        String username = authentication.getName();
        logger.debug("Fetching current user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User Not Found in DB after successful authentication: {}", username);
                    return new UsernameNotFoundException("User Not Found with username: " + username);
                });
    }

    // UserProfile sekarang bagian dari User, jadi tidak perlu argumen UserProfile terpisah
    private UserProfileDto convertToDto(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setUserId(user.getId()); // ID String
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());

        UserProfile profile = user.getUserProfile(); // Ambil dari User object
        if (profile != null) {
            dto.setFullName(profile.getFullName());
            dto.setPhoneNumber(profile.getPhoneNumber());
            dto.setAddress(profile.getAddress());
        } else {
            // Set field DTO ke null atau default jika profile belum ada
            dto.setFullName(null);
            dto.setPhoneNumber(null);
            dto.setAddress(null);
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public UserProfileDto getUserProfile() {
        User currentUser = getCurrentUser();
        logger.info("Fetching profile for user ID: {}", currentUser.getId());
        // UserProfile sudah ada di dalam currentUser jika sudah diisi
        // Tidak perlu query ke UserProfileRepository
        return convertToDto(currentUser);
    }

    @Transactional
    public UserProfileDto updateUserProfile(UserProfileDto profileUpdateRequest) {
        User currentUser = getCurrentUser();
        logger.info("Attempting to update profile for user ID: {}", currentUser.getId());

        UserProfile profile = currentUser.getUserProfile();
        if (profile == null) {
            profile = new UserProfile();
            currentUser.setUserProfile(profile); // Set profile baru ke objek user
        }

        profile.setFullName(profileUpdateRequest.getFullName());
        profile.setPhoneNumber(profileUpdateRequest.getPhoneNumber());
        profile.setAddress(profileUpdateRequest.getAddress());

        // Simpan objek User utama, yang juga akan menyimpan UserProfile yang di-embed
        User savedUser = userRepository.save(currentUser);
        logger.info("Profile updated and user saved successfully for user ID: {}", savedUser.getId());

        return convertToDto(savedUser);
    }
}