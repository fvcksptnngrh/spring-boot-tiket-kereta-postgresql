package com.example.kaiservice.controller;

import com.example.kaiservice.dto.StationDto;
import com.example.kaiservice.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stations") // Base path untuk endpoint stasiun
public class StationController {

    @Autowired
    private StationService stationService;

    // Endpoint untuk mendapatkan semua stasiun
    @GetMapping
    public ResponseEntity<List<StationDto>> getAllStations() {
        List<StationDto> stations = stationService.getAllStations();
        if (stations.isEmpty()) {
            // Jika tidak ada stasiun, kembalikan response Not Found atau OK dengan list kosong
            return ResponseEntity.ok(stations); // Atau ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(stations); // Kembalikan daftar stasiun dengan status OK (200)
    }

    // Opsional: Endpoint untuk menambahkan stasiun baru (jika diperlukan)
    // @PostMapping
    // public ResponseEntity<StationDto> addStation(@RequestBody StationDto stationDto) {
    //     try {
    //          StationDto newStation = stationService.addStation(stationDto);
    //          return ResponseEntity.status(HttpStatus.CREATED).body(newStation); // Status CREATED (201)
    //     } catch(Exception e) {
    //          // Handle error, misal stasiun sudah ada
    //          return ResponseEntity.badRequest().build();
    //     }
    // }
}