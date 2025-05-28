package com.example.kaiservice.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ScheduleResponseDto {
    private String id; // Diubah dari Long ke String
    private String departureStationName;
    private String departureStationCity;
    private String arrivalStationName;
    private String arrivalStationCity;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String trainName;
    private Double price;
    private Integer availableSeats;
}