package com.example.kaiservice.security;

import com.example.kaiservice.entity.Role; // <-- Tambahkan import untuk Role
import com.example.kaiservice.entity.User;
import com.example.kaiservice.repository.UserRepository;
import org.slf4j.Logger; // <-- Tambahkan import untuk Logger
import org.slf4j.LoggerFactory; // <-- Tambahkan import untuk LoggerFactory
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority; // <-- Tambahkan import
import org.springframework.security.core.authority.SimpleGrantedAuthority; // <-- Tambahkan import
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List; // <-- Tambahkan import
import java.util.Set; // <-- Tambahkan import
import java.util.stream.Collectors; // <-- Tambahkan import

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    // Tambahkan logger untuk debugging
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        // --- BAGIAN YANG DIPERBAIKI ---
        // Ambil Set<Role> dari objek User
        Set<Role> roles = user.getRoles();

        // Ubah Set<Role> menjadi List<GrantedAuthority>
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName())) // Ambil nama peran (misal "ROLE_ADMIN")
                .collect(Collectors.toList());

        // LOG PENTING UNTUK MELIHAT APAKAH PERAN BERHASIL DIMUAT
        logger.info("Username: " + user.getUsername() + " - Authorities loaded: " + authorities);
        if (authorities.isEmpty()) {
            logger.warn("WARNING: No authorities loaded for user " + user.getUsername() +
                        ". Check database (user_roles table) and User entity mapping.");
        }
        // --- SELESAI BAGIAN YANG DIPERBAIKI ---

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities // <-- Gunakan authorities yang sudah dimuat dari database
        );
    }
}