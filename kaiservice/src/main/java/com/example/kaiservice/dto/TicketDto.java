package com.example.kaiservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TicketDto {
    private Long id;
    private Long userId;
    private ScheduleDto schedule; // Tampilkan detail jadwal
    private LocalDateTime bookingTime;
    private String seatNumber;
    private String status; // Tampilkan status sebagai String
}