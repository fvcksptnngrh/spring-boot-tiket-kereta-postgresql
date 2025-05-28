package com.example.kaiservice.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.kaiservice.entity.Schedule; // Pastikan impor ini ada

@Repository
public interface ScheduleRepository extends MongoRepository<Schedule, String> {

    // Metode yang sudah ada
    List<Schedule> findByDepartureStationInfo_StationIdAndArrivalStationInfo_StationIdAndDepartureTimeBetween(
            String departureStationId,
            String arrivalStationId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    List<Schedule> findByDepartureTimeAfter(LocalDateTime time);

    List<Schedule> findByTrainName(String trainName); // Metode ini ada di kode saya sebelumnya, pastikan ada jika Anda menggunakannya

    Optional<Schedule> findByDepartureStationInfo_StationIdAndArrivalStationInfo_StationIdAndTrainNameAndDepartureTime(
            String departureStationId,
            String arrivalStationId,
            String trainName,
            LocalDateTime departureTime
    );
}