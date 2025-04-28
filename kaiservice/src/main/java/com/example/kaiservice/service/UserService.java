package com.example.kaiservice.service;

import com.example.kaiservice.dto.UserProfileDto;
import com.example.kaiservice.entity.User;
import com.example.kaiservice.entity.UserProfile;
import com.example.kaiservice.repository.UserProfileRepository;
import com.example.kaiservice.repository.UserRepository;

// Import Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    // Tambahkan logger
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    // Helper method untuk mendapatkan User yang sedang login
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            logger.warn("Attempted to get current user but no authentication found or user is anonymous.");
            throw new RuntimeException("No authenticated user found. Please login."); // Atau exception yang lebih spesifik
        }
        String username = authentication.getName(); // Dapatkan username dari principal
        logger.debug("Fetching current user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User Not Found in DB after successful authentication: {}", username); // Seharusnya tidak terjadi
                    return new UsernameNotFoundException("User Not Found with username: " + username);
                });
    }

    // Helper method konversi User + UserProfile ke DTO
    private UserProfileDto convertToDto(User user, UserProfile profile) {
        UserProfileDto dto = new UserProfileDto();
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        if (profile != null) { // Handle jika profile belum ada
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

    // Mendapatkan profil user yang sedang login
    @Transactional(readOnly = true)
    public UserProfileDto getUserProfile() {
        User currentUser = getCurrentUser(); // Dapatkan user yang login
        logger.info("Fetching profile for user ID: {}", currentUser.getId());
        // Cari profile berdasarkan user ID
        Optional<UserProfile> profileOpt = userProfileRepository.findByUser_Id(currentUser.getId());
        if(profileOpt.isPresent()) {
            logger.info("Profile found for user ID: {}", currentUser.getId());
        } else {
             logger.info("Profile not found for user ID: {}. Returning user data only.", currentUser.getId());
        }
        return convertToDto(currentUser, profileOpt.orElse(null)); // Kirim null jika profile belum ada
    }

    // Mengupdate atau membuat profil user yang sedang login (SUDAH DIPERBAIKI)
    @Transactional // Operasi tulis, jadi perlu transaksi
    public UserProfileDto updateUserProfile(UserProfileDto profileUpdateRequest) {
        User currentUser = getCurrentUser(); // Dapatkan user yang login
        logger.info("Attempting to update profile for user ID: {}", currentUser.getId());

        // Cari profil yang sudah ada, ATAU siapkan profil baru jika belum ada
        UserProfile profile = userProfileRepository.findByUser_Id(currentUser.getId())
                .orElseGet(() -> { // Gunakan orElseGet untuk membuat instance baru
                    logger.info("Profile not found for user ID: {}. Creating new profile.", currentUser.getId());
                    UserProfile newProfile = new UserProfile();
                    // PENTING: Asosiasikan User ke profil baru INI
                    newProfile.setUser(currentUser);
                    // JANGAN atur ID secara manual di sini: newProfile.setId(currentUser.getId());
                    return newProfile;
                });

        // Update field-field profil dari data request DTO
        logger.debug("Updating profile fields for user ID: {}", currentUser.getId());
        profile.setFullName(profileUpdateRequest.getFullName());
        profile.setPhoneNumber(profileUpdateRequest.getPhoneNumber());
        profile.setAddress(profileUpdateRequest.getAddress());

        // Simpan profil.
        logger.info("Saving profile for user ID: {}", currentUser.getId());
        UserProfile savedProfile = userProfileRepository.save(profile);
        logger.info("Profile saved successfully for user ID: {}", currentUser.getId());

        // Kembalikan DTO dari user saat ini dan profil yang sudah tersimpan/diupdate
        return convertToDto(currentUser, savedProfile);
    }
}