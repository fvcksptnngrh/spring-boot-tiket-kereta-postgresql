package com.example.kaiservice.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.kaiservice.dto.ScheduleRequestDto;
import com.example.kaiservice.dto.ScheduleResponseDto;
import com.example.kaiservice.service.ScheduleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleController.class);

    @Autowired
    private ScheduleService scheduleService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScheduleResponseDto> createSchedule(@Valid @RequestBody ScheduleRequestDto scheduleRequestDto) {
        logger.info("Received request to create schedule with train name: {}", scheduleRequestDto.getTrainName());
        ScheduleResponseDto createdSchedule = scheduleService.createSchedule(scheduleRequestDto);
        return new ResponseEntity<>(createdSchedule, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScheduleResponseDto> updateSchedule(@PathVariable String id, @Valid @RequestBody ScheduleRequestDto scheduleRequestDto) { // ID menjadi String
        logger.info("Received request to update schedule ID: {}", id);
        ScheduleResponseDto updatedSchedule = scheduleService.updateSchedule(id, scheduleRequestDto);
        return ResponseEntity.ok(updatedSchedule);
    }

    @GetMapping
    @PreAuthorize("permitAll()") // Atau @PreAuthorize("isAuthenticated()") sesuai kebutuhan
    public ResponseEntity<List<ScheduleResponseDto>> getAllSchedules() {
        logger.info("Received request to get all schedules");
        List<ScheduleResponseDto> schedules = scheduleService.getAllSchedules();
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()") // Atau @PreAuthorize("isAuthenticated()") sesuai kebutuhan
    public ResponseEntity<ScheduleResponseDto> getScheduleById(@PathVariable String id) { // ID menjadi String
        logger.info("Received request to get schedule by ID: {}", id);
        ScheduleResponseDto schedule = scheduleService.getScheduleById(id);
        return ResponseEntity.ok(schedule);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSchedule(@PathVariable String id) { // ID menjadi String
        logger.info("Received request to delete schedule ID: {}", id);
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
    
    // Endpoint untuk seats (jika ada dan menggunakan ID schedule)
    // @GetMapping("/{id}/seats")
    // @PreAuthorize("permitAll()")
    // public ResponseEntity<?> getScheduleSeats(@PathVariable String id) { ... }
}