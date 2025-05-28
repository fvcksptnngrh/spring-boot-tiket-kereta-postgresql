package com.example.kaiservice.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// Impor EmbeddedStationInfo jika belum otomatis ditambahkan oleh IDE
// import com.example.kaiservice.entity.EmbeddedStationInfo; // Seharusnya tidak perlu jika di package yang sama dan public

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "schedules")
public class Schedule {
    @Id
    private String id;

    // Menggunakan kelas EmbeddedStationInfo yang sudah public dan terpisah
    private EmbeddedStationInfo departureStationInfo;
    private EmbeddedStationInfo arrivalStationInfo;

    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String trainName;
    private Double price;
    private Integer availableSeats;
}