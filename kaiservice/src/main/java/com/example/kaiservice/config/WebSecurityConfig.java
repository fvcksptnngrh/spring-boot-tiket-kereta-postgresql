package com.example.kaiservice.config;

import com.example.kaiservice.security.AuthEntryPointJwt;
import com.example.kaiservice.security.AuthTokenFilter;
// import com.example.kaiservice.security.UserDetailsServiceImpl; // Tidak perlu di-inject langsung ke sini jika AuthenticationManager dikonfigurasi melalui AuthenticationConfiguration
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.dao.DaoAuthenticationProvider; // Lihat catatan di bawah
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
@EnableMethodSecurity // PENTING untuk @PreAuthorize di Controller
public class WebSecurityConfig {

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    // Bean untuk AuthTokenFilter sudah benar
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    // Bean untuk PasswordEncoder sudah benar
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean untuk AuthenticationManager sudah benar
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Catatan: DaoAuthenticationProvider akan otomatis dikonfigurasi oleh Spring Boot
    // jika UserDetailsService Anda (UserDetailsServiceImpl) tersedia sebagai bean
    // dan PasswordEncoder juga tersedia sebagai bean. Jadi, bean DaoAuthenticationProvider
    // eksplisit di sini mungkin tidak selalu diperlukan kecuali Anda ingin kustomisasi lebih.
    // Kita bisa coba tanpa bean DaoAuthenticationProvider eksplisit dulu.

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll() // Registrasi & Login publik

                // --- USER & PROFILE Management ---
                // User bisa akses profilnya sendiri (GET & POST/PUT)
                .requestMatchers(HttpMethod.GET, "/api/users/profile").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/users/profile").authenticated() // Atau PUT jika Anda ganti
                // Endpoint lain di bawah /api/users/ akan diatur dengan @PreAuthorize("hasRole('ADMIN')") di UserController
                // jadi aturan umum di sini bisa lebih longgar (authenticated) atau lebih ketat (ADMIN)
                // Mari kita buat spesifik untuk admin jika mengakses path umum /api/users
                .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN") // Untuk list semua user oleh admin
                // Untuk /api/users/{userId} (CRUD user lain oleh Admin) akan diproteksi dengan @PreAuthorize

                // --- STATION Management ---
                // Semua user (termasuk anonim jika permitAll, atau yang login jika authenticated) boleh lihat stasiun
                .requestMatchers(HttpMethod.GET, "/api/stations").permitAll() // Atau .authenticated()
                .requestMatchers(HttpMethod.GET, "/api/stations/{id:\\d+}").permitAll() // Atau .authenticated()
                // Admin bisa CRUD stasiun (ini akan diatur dengan @PreAuthorize di StationController)
                // Jadi kita bisa buat /api/stations/** secara umum authenticated, lalu PreAuthorize yang perketat
                // Atau definisikan eksplisit di sini:
                .requestMatchers(HttpMethod.POST, "/api/stations").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/stations/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/stations/**").hasRole("ADMIN")

                // --- SCHEDULE Management ---
                // Semua user (termasuk anonim jika permitAll, atau yang login jika authenticated) boleh lihat jadwal
                .requestMatchers(HttpMethod.GET, "/api/schedules").permitAll() // Atau .authenticated()
                .requestMatchers(HttpMethod.GET, "/api/schedules/{id:\\d+}").permitAll() // Atau .authenticated()
                .requestMatchers(HttpMethod.GET, "/api/schedules/{id:\\d+}/seats").permitAll() // Atau .authenticated()
                // Admin bisa CRUD jadwal (ini akan diatur dengan @PreAuthorize di ScheduleController)
                // Jadi kita bisa buat /api/schedules/** secara umum authenticated, lalu PreAuthorize yang perketat
                // Atau definisikan eksplisit di sini:
                .requestMatchers(HttpMethod.POST, "/api/schedules").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/schedules/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/schedules/**").hasRole("ADMIN")

                // --- TICKET Management ---
                // Semua akses ke /api/tickets/** memerlukan user untuk terotentikasi.
                // Detail siapa boleh apa akan diatur dengan @PreAuthorize di TicketController.
                .requestMatchers("/api/tickets/**").authenticated()

                .anyRequest().authenticated() // Semua request lain yang tidak cocok di atas harus terotentikasi
            );

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}