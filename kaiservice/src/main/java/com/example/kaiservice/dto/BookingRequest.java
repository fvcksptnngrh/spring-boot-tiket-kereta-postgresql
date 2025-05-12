package com.example.kaiservice.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull; // Import untuk @NotNull
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
// import jakarta.validation.constraints.Pattern; //   // Import untuk @Size (opsional untuk seatNumber)

@Data
public class BookingRequest {

    @NotNull(message = "ID Jadwal tidak boleh kosong") // scheduleId wajib ada
    private Long scheduleId;

    @NotBlank(message = "Nomor kursi tidak boleh kosong")
    @Size(max = 10, message = "Nomor kursi maksimal 10 karakter") // Contoh batasan panjang jika diisi
    private String seatNumber; // Bisa tetap opsional (boleh null jika tidak diisi dari frontend)

    // Jika 1 booking bisa > 1 penumpang, tambahkan list penumpang atau count
    @Min(value = 1, message = "Jumlah penumpang minimal 1")
    private int passengerCount = 1; // Default ke 1 jika tidak diisi
}