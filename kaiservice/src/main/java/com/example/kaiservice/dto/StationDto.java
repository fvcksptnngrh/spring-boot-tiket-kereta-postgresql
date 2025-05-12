package com.example.kaiservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank; // Import untuk validasi
import jakarta.validation.constraints.Size;    // Import untuk validasi

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StationDto {
    private Long id; // Biarkan null saat create

    @NotBlank(message = "Nama stasiun tidak boleh kosong") // Tidak boleh null dan tidak boleh hanya spasi
    @Size(min = 3, max = 100, message = "Nama stasiun harus antara 3 dan 100 karakter")
    private String name;

    @NotBlank(message = "Nama kota tidak boleh kosong")
    @Size(min = 3, max = 50, message = "Nama kota harus antara 3 dan 50 karakter")
    private String city;
}