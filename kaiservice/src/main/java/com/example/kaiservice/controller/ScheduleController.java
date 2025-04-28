package com.example.kaiservice.controller;

import com.example.kaiservice.dto.ScheduleDto;
import com.example.kaiservice.dto.SeatDto;
import com.example.kaiservice.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat; // Import DateTimeFormat
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // Import anotasi web

import java.time.LocalDate; // Import LocalDate
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/schedules") // Base path untuk endpoint jadwal
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    // Endpoint untuk mendapatkan jadwal (semua atau terfilter)
    @GetMapping
    public ResponseEntity<List<ScheduleDto>> findSchedules(
            @RequestParam(required = false) Long originStationId,
            @RequestParam(required = false) Long destinationStationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate) {

        List<ScheduleDto> schedules = scheduleService.findSchedules(originStationId, destinationStationId, departureDate);
        return ResponseEntity.ok(schedules); // Selalu kembalikan OK, list bisa kosong
    }

    // Endpoint untuk mendapatkan detail jadwal spesifik
    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleDto> getScheduleById(@PathVariable Long scheduleId) {
        Optional<ScheduleDto> scheduleDtoOpt = scheduleService.getScheduleById(scheduleId);

        // Jika ditemukan, kembalikan OK (200) dengan datanya
        // Jika tidak, kembalikan Not Found (404)
        return scheduleDtoOpt.map(ResponseEntity::ok) // Jika ada, bungkus dengan ResponseEntity.ok()
                             .orElseGet(() -> ResponseEntity.notFound().build()); // Jika kosong, buat ResponseEntity 404
    }

    // Endpoint untuk mendapatkan ketersediaan kursi jadwal spesifik
    @GetMapping("/{scheduleId}/seats")
    public ResponseEntity<List<SeatDto>> getAvailableSeats(@PathVariable Long scheduleId) {
         Optional<List<SeatDto>> seatsOpt = scheduleService.getAvailableSeats(scheduleId);

         // Jika jadwal ditemukan dan ada data kursi, kembalikan OK (200)
         // Jika jadwal tidak ditemukan, kembalikan Not Found (404)
         return seatsOpt.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.notFound().build());
    }
}