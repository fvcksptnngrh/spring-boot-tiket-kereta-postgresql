package com.example.kaiservice.config; // Atau package config Anda

import com.example.kaiservice.security.AuthEntryPointJwt;
import com.example.kaiservice.security.AuthTokenFilter; // Pastikan ini adalah kelas filter Anda
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Opsional: Jika Anda menggunakan @PreAuthorize di controller
public class WebSecurityConfig { // Nama kelas bisa berbeda

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    // Pastikan Anda membuat AuthTokenFilter sebagai Bean atau meng-injectnya dengan benar
    // Salah satu cara adalah membuat bean-nya di sini:
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter(); // JwtUtils dan UserDetailsServiceImpl akan di-autowired di dalam AuthTokenFilter
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
    .authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/auth/**").permitAll()
    .requestMatchers("/api/users/**").hasRole("ADMIN") // Jika semua di bawah /api/users hanya untuk admin
    .requestMatchers("/api/stations/**").hasRole("ADMIN") // Mengamankan semua endpoint stasiun untuk ADMIN
    .requestMatchers(HttpMethod.GET, "/api/schedules").authenticated() // Jika semua user boleh GET
    .requestMatchers(HttpMethod.GET, "/api/schedules/{id:\\d+}").authenticated() // Jika semua user boleh GET by ID
    .requestMatchers(HttpMethod.POST, "/api/schedules").hasRole("ADMIN")
    .requestMatchers(HttpMethod.PUT, "/api/schedules/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.DELETE, "/api/schedules/**").hasRole("ADMIN")
    // ... aturan lain untuk /api/schedules/**, /api/tickets/**
    .anyRequest().authenticated()
            );

        http.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler));
            
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}