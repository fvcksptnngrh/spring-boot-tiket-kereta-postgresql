package com.example.kaiservice.repository;

import com.example.kaiservice.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {
    // Bisa tambahkan query custom jika perlu, misal findByCity
}