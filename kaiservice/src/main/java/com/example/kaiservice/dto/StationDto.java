package com.example.kaiservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StationDto {
    private String id; // Diubah dari Long ke String. Biarkan null saat create jika ID di-generate server.

    @NotBlank(message = "Nama stasiun tidak boleh kosong")
    @Size(min = 3, max = 100, message = "Nama stasiun harus antara 3 dan 100 karakter")
    private String name;

    @NotBlank(message = "Nama kota tidak boleh kosong")
    @Size(min = 3, max = 50, message = "Nama kota harus antara 3 dan 50 karakter")
    private String city;
}