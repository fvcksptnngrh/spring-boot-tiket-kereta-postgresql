package com.example.kaiservice.dto;

import lombok.Data;

@Data
public class BookingRequest {
    private Long scheduleId;
    private String seatNumber; // Opsional, tergantung alur bisnis
    // Jika 1 booking bisa > 1 penumpang, tambahkan list penumpang atau count
    // private int passengerCount;
}