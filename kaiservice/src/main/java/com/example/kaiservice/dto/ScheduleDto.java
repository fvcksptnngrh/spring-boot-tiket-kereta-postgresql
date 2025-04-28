package com.example.kaiservice.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ScheduleDto {
    private Long id;
    private StationDto originStation; // Gunakan DTO stasiun
    private StationDto destinationStation; // Gunakan DTO stasiun
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String trainName;
    private BigDecimal price;
    private int availableSeats;
}