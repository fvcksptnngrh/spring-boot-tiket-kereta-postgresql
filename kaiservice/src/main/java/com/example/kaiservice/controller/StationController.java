package com.example.kaiservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // Tambahkan jika Anda menggunakan @Valid pada DTO
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.kaiservice.dto.StationDto;
import com.example.kaiservice.service.StationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/stations")
public class StationController {

    @Autowired
    private StationService stationService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StationDto> createStation(@Valid @RequestBody StationDto stationDto) {
        StationDto createdStation = stationService.createStation(stationDto);
        return new ResponseEntity<>(createdStation, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("permitAll()") // Atau sesuai kebutuhan akses Anda
    public ResponseEntity<List<StationDto>> getAllStations() {
        List<StationDto> stations = stationService.getAllStations();
        return ResponseEntity.ok(stations);
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()") // Atau @PreAuthorize("hasRole('ADMIN')") atau @PreAuthorize("isAuthenticated()")
    public ResponseEntity<StationDto> getStationById(@PathVariable String id) { // ID menjadi String
        StationDto station = stationService.getStationById(id);
        return ResponseEntity.ok(station);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StationDto> updateStation(@PathVariable String id, @Valid @RequestBody StationDto stationDto) { // ID menjadi String
        StationDto updatedStation = stationService.updateStation(id, stationDto);
        return ResponseEntity.ok(updatedStation);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStation(@PathVariable String id) { // ID menjadi String
        stationService.deleteStation(id);
        return ResponseEntity.noContent().build();
    }
}