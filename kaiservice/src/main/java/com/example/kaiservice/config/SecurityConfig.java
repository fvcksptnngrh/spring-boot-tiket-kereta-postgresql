package com.example.kaiservice.config;

import com.example.kaiservice.security.AuthEntryPointJwt;
import com.example.kaiservice.security.AuthTokenFilter;
import com.example.kaiservice.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // Import EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // Menandakan kelas konfigurasi Spring
@EnableWebSecurity // Mengaktifkan konfigurasi keamanan web Spring (penting!)
@EnableMethodSecurity // Opsional: Mengaktifkan anotasi security pada level method (@PreAuthorize, dll.)
public class SecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService; // Service untuk load user details

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler; // Handler untuk error otentikasi

    @Bean // Membuat bean AuthTokenFilter agar bisa di-inject Autowired
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean // Konfigurasi Authentication Provider (DAO = Data Access Object)
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService); // Set service user details
        authProvider.setPasswordEncoder(passwordEncoder()); // Set encoder password

        return authProvider;
    }

    @Bean // Membuat bean AuthenticationManager
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean // Membuat bean PasswordEncoder (BCrypt direkomendasikan)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean // Konfigurasi utama HttpSecurity (rantai filter keamanan)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Menonaktifkan CSRF (umum untuk API stateless)
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)) // Set entry point untuk error 401
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Buat session stateless (tidak menyimpan state di server)
            .authorizeHttpRequests(auth ->
                auth.requestMatchers("/api/auth/**").permitAll() // Izinkan akses ke endpoint /api/auth/... (login, register)
                    .requestMatchers("/api/stations/**").permitAll() // Izinkan akses ke daftar stasiun
                    .requestMatchers("/api/schedules/**").permitAll() // Izinkan akses ke daftar jadwal & kursi
                    .requestMatchers("/h2-console/**").permitAll() // Izinkan akses ke H2 Console (HANYA UNTUK DEVELOPMENT!)
                    .anyRequest().authenticated() // Semua request lain memerlukan otentikasi
            );

        // Penting untuk H2 console agar bisa diakses dalam frame
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        // Set authentication provider
        http.authenticationProvider(authenticationProvider());

        // Tambahkan filter JWT sebelum filter UsernamePasswordAuthenticationFilter
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build(); // Bangun SecurityFilterChain
    }
}