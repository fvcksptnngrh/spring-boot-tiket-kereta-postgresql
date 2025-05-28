package com.example.kaiservice.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TicketDto {
    private String id;
    private String userId;
    private String username; 

    private ScheduleResponseDto schedule;

    private LocalDateTime bookingTime;
    private String seatNumber;
    private String status;

    private Integer passengerCount; 
    private Double totalPrice; 
}