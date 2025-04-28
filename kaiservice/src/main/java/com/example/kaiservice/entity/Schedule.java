package com.example.kaiservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime; // Gunakan tipe data waktu modern
import java.math.BigDecimal; // Gunakan BigDecimal untuk harga

@Entity
@Table(name = "schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // Relasi ke Station (Asal)
    @JoinColumn(name = "origin_station_id", nullable = false)
    private Station originStation;

    @ManyToOne // Relasi ke Station (Tujuan)
    @JoinColumn(name = "destination_station_id", nullable = false)
    private Station destinationStation;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    @Column(nullable = false)
    private String trainName;

    @Column(nullable = false, precision = 10, scale = 2) // Presisi untuk uang
    private BigDecimal price;

    private int availableSeats; // Jumlah kursi tersedia (bisa di-update saat booking)
}