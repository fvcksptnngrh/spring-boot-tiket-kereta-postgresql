package com.example.kaiservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BookingRequest {

    @NotBlank(message = "ID Jadwal tidak boleh kosong")
    private String scheduleId; 

    @NotBlank(message = "Nomor kursi tidak boleh kosong")
    @Size(max = 10, message = "Nomor kursi maksimal 10 karakter")
    private String seatNumber;

    @Min(value = 1, message = "Jumlah penumpang minimal 1")
    private int passengerCount = 1;
}