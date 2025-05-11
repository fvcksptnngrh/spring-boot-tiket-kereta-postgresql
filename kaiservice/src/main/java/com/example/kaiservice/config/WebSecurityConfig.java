package com.example.kaiservice.config; // Atau package config Anda

import com.example.kaiservice.security.AuthEntryPointJwt;
import com.example.kaiservice.security.AuthTokenFilter; // Pastikan ini adalah kelas filter Anda
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth // <--- BAGIAN INI YANG KITA PERLU LIHAT
                .requestMatchers("/api/auth/**").permitAll() // Contoh: login dan register
                // BAGAIMANA ANDA MENGATUR AKSES UNTUK /api/users ?
                // .requestMatchers("/api/users").permitAll() // Apakah seperti ini? (Tidak aman jika berisi data sensitif)
                // .requestMatchers("/api/users").authenticated() // Apakah seperti ini? (Harusnya bisa jika peran sudah benar)
                .requestMatchers("/api/users").hasRole("ADMIN") // Apakah seperti ini? (Ini yang kita harapkan untuk user ADMIN)
                // .requestMatchers("/api/users").hasAuthority("ROLE_ADMIN") // Atau seperti ini?
                .anyRequest().authenticated() // Semua request lain minimal harus terotentikasi
            );

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}