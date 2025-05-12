package com.example.kaiservice.repository;

import com.example.kaiservice.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // Contoh query custom untuk mencari jadwal berdasarkan asal, tujuan, dan tanggal
    List<Schedule> findByDepartureStationIdAndArrivalStationIdAndDepartureTimeBetween(
            Long departureStationId,
            Long arrivalStationId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

     List<Schedule> findByDepartureTimeAfter(LocalDateTime time); // Cari jadwal setelah waktu tertentu
}