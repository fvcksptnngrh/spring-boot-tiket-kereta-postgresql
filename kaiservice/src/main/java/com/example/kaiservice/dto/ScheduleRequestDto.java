package com.example.kaiservice.dto;

import lombok.Data;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*; 

@Data
public class ScheduleRequestDto {

    @NotNull(message = "Departure station ID cannot be null")
    private Long departureStationId;

    @NotNull(message = "Arrival station ID cannot be null")
    private Long arrivalStationId;

    @NotNull(message = "Departure time cannot be null")
     @Future(message = "Departure time must be in the future") // Contoh validasi
    private LocalDateTime departureTime;

    @NotNull(message = "Arrival time cannot be null")
    @Future(message = "Arrival time must be in the future") // Contoh validasi
    private LocalDateTime arrivalTime;

    @NotBlank(message = "Train name cannot be blank")
    private String trainName;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be positive")
    private Double price;
    
    @NotNull(message = "Available seats cannot be null")
    @Min(value = 0, message = "Available seats cannot be negative")
    private Integer availableSeats;
}