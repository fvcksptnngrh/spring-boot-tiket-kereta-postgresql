package com.example.kaiservice.repository;

import com.example.kaiservice.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
     Optional<UserProfile> findByUser_Id(Long userId); // Cari profile berdasarkan ID user
}