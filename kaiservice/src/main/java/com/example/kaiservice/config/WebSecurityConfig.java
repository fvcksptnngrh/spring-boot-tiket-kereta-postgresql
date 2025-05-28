package com.example.kaiservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

import com.example.kaiservice.security.AuthEntryPointJwt;
import com.example.kaiservice.security.AuthTokenFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    private static final String OBJECT_ID_REGEX = "[a-fA-F0-9]{24}"; // Regex untuk MongoDB ObjectId

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
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
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()

                .requestMatchers(HttpMethod.GET, "/api/users/profile").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/users/profile").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")

                .requestMatchers(HttpMethod.GET, "/api/stations").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/stations/{id:" + OBJECT_ID_REGEX + "}").permitAll() // Regex diubah
                .requestMatchers(HttpMethod.POST, "/api/stations").hasRole("ADMIN")
                // Jika PUT dan DELETE menggunakan path variable ID spesifik, tambahkan regex juga
                // Contoh: .requestMatchers(HttpMethod.PUT, "/api/stations/{id:" + OBJECT_ID_REGEX + "}").hasRole("ADMIN")
                // Namun, karena Anda menggunakan /**, ini mungkin sudah tercakup, tetapi lebih baik eksplisit jika ada override.
                .requestMatchers(HttpMethod.PUT, "/api/stations/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/stations/**").hasRole("ADMIN")

                .requestMatchers(HttpMethod.GET, "/api/schedules").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/schedules/{id:" + OBJECT_ID_REGEX + "}").permitAll() // Regex diubah
                .requestMatchers(HttpMethod.GET, "/api/schedules/{id:" + OBJECT_ID_REGEX + "}/seats").permitAll() // Regex diubah
                .requestMatchers(HttpMethod.POST, "/api/schedules").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/schedules/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/schedules/**").hasRole("ADMIN")

                .requestMatchers("/api/tickets/**").authenticated() // Aturan umum untuk tiket

                .anyRequest().authenticated()
            );

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}