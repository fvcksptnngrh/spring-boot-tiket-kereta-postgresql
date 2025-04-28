package com.example.kaiservice.security;

import com.example.kaiservice.entity.User;
import com.example.kaiservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList; // Import ArrayList

@Service // Tandai sebagai Spring service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional // Dibutuhkan jika ada relasi LAZY yang perlu diakses
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        // Spring Security UserDetails (menggunakan implementasi bawaan User)
        // Kita belum mendefinisikan Roles, jadi kita gunakan empty list untuk authorities
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(), // Password yg sudah di-hash dari DB
                new ArrayList<>() // List kosong untuk GrantedAuthority (Roles)
        );
        // Jika Anda sudah implementasi Roles, mapping Roles ke GrantedAuthority di sini
    }
}