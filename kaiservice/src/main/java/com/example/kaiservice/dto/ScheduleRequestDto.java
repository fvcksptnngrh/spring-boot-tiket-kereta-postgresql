package com.example.kaiservice.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ScheduleRequestDto {

    @NotBlank(message = "Departure station ID cannot be blank") // Diubah dari @NotNull dan tipe ke String
    private String departureStationId;

    @NotBlank(message = "Arrival station ID cannot be blank") // Diubah dari @NotNull dan tipe ke String
    private String arrivalStationId;

    @NotNull(message = "Departure time cannot be null")
    @Future(message = "Departure time must be in the future")
    private LocalDateTime departureTime;

    @NotNull(message = "Arrival time cannot be null")
    @Future(message = "Arrival time must be in the future")
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