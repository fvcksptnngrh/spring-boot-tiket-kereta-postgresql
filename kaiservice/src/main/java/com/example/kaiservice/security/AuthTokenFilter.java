package com.example.kaiservice.security;

// Import logger (pilih salah satu)
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// atau jika pakai Lombok: import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// Import lain yang sudah ada...
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// @Slf4j // Jika pakai Lombok
public class AuthTokenFilter extends OncePerRequestFilter {
    // Jika tidak pakai Lombok, buat logger manual:
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // === LOG AWAL ===
        logger.info(">>> AuthTokenFilter: Processing request for: {}", request.getRequestURI());
        try {
            String jwt = parseJwt(request);
            if (jwt != null) {
                // === LOG JIKA TOKEN DITEMUKAN ===
                logger.info(">>> AuthTokenFilter: JWT found in header. Validating...");
                if (jwtUtils.validateJwtToken(jwt)) { // Validasi token
                    // === LOG JIKA VALIDASI SUKSES ===
                    logger.info(">>> AuthTokenFilter: JWT validation successful.");
                    String username = jwtUtils.getUserNameFromJwtToken(jwt);
                    logger.info(">>> AuthTokenFilter: Username extracted from JWT: {}", username);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    // Log authorities dari userDetails
                    logger.info(">>> AuthTokenFilter: User authorities for " + username + ": " + userDetails.getAuthorities());

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()); // Pastikan authorities disertakan
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info(">>> AuthTokenFilter: Authentication object created with authorities: " + authentication.getAuthorities());
                } else {
                    // === LOG JIKA VALIDASI GAGAL ===
                    logger.warn(">>> AuthTokenFilter: JWT validation returned false.");
                }
            } else {
                 // === LOG JIKA TOKEN TIDAK DITEMUKAN ===
                 logger.info(">>> AuthTokenFilter: No JWT found in Authorization header for URI: {}", request.getRequestURI());
            }
        } catch (Exception e) {
             // === LOG JIKA ADA EXCEPTION UMUM ===
             logger.error(">>> AuthTokenFilter: Cannot set user authentication", e);
        }

        filterChain.doFilter(request, response);
        // === LOG AKHIR ===
        // logger.info(">>> AuthTokenFilter: Finished processing request for: {}", request.getRequestURI());
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        // logger.debug(">>> AuthTokenFilter: Raw Authorization Header: {}", headerAuth); // Log detail jika perlu

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
             String token = headerAuth.substring(7);
             // logger.debug(">>> AuthTokenFilter: Parsed JWT: {}", token); // Log detail jika perlu
             return token;
        }
        return null;
    }
}