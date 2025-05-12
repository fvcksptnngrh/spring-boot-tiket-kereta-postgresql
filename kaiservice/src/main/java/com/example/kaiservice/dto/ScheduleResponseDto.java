package com.example.kaiservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ScheduleResponseDto {
    private Long id;
    private String departureStationName; // Kita akan isi nama stasiunnya
    private String departureStationCity; // Tambahkan kota jika perlu
    private String arrivalStationName;   // Kita akan isi nama stasiunnya
    private String arrivalStationCity;   // Tambahkan kota jika perlu
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String trainName;
    private Double price;
    private Integer availableSeats;
}