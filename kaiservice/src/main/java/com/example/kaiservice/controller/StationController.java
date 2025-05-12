package com.example.kaiservice.controller;

import com.example.kaiservice.dto.StationDto;
import com.example.kaiservice.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stations") // Base path untuk stasiun
public class StationController {

    @Autowired
    private StationService stationService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Hanya ADMIN yang bisa membuat stasiun
    public ResponseEntity<StationDto> createStation(@RequestBody StationDto stationDto) {
        // Tambahkan validasi untuk stationDto jika pakai @Valid di DTO
        StationDto createdStation = stationService.createStation(stationDto);
        return new ResponseEntity<>(createdStation, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("permitAll()") // Hanya ADMIN yang bisa melihat semua stasiun
    // Jika semua user boleh lihat, ganti jadi @PreAuthorize("isAuthenticated()") atau hapus jika publik
    public ResponseEntity<List<StationDto>> getAllStations() {
        List<StationDto> stations = stationService.getAllStations();
        return ResponseEntity.ok(stations);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Hanya ADMIN yang bisa melihat stasiun by ID
    public ResponseEntity<StationDto> getStationById(@PathVariable Long id) {
        StationDto station = stationService.getStationById(id);
        return ResponseEntity.ok(station);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Hanya ADMIN yang bisa update stasiun
    public ResponseEntity<StationDto> updateStation(@PathVariable Long id, @RequestBody StationDto stationDto) {
        StationDto updatedStation = stationService.updateStation(id, stationDto);
        return ResponseEntity.ok(updatedStation);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Hanya ADMIN yang bisa delete stasiun
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.deleteStation(id);
        return ResponseEntity.noContent().build();
    }

    
}