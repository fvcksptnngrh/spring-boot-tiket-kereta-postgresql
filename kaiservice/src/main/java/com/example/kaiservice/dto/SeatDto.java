package com.example.kaiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatDto {
    private String seatNumber;
    private String status; // Misal: "AVAILABLE", "BOOKED"
}