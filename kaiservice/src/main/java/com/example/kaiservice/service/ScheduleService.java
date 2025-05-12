package com.example.kaiservice.service;

import com.example.kaiservice.dto.ScheduleRequestDto;
import com.example.kaiservice.dto.ScheduleResponseDto;
import com.example.kaiservice.entity.Schedule;
import com.example.kaiservice.entity.Station;
import com.example.kaiservice.exception.ResourceNotFoundException; // IMPORT CUSTOM EXCEPTION
import com.example.kaiservice.repository.ScheduleRepository;
import com.example.kaiservice.repository.StationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleService.class);

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private StationRepository stationRepository;

    private ScheduleResponseDto convertToResponseDto(Schedule schedule) {
        ScheduleResponseDto dto = new ScheduleResponseDto();
        dto.setId(schedule.getId());
        if (schedule.getDepartureStation() != null) {
            dto.setDepartureStationName(schedule.getDepartureStation().getName());
            dto.setDepartureStationCity(schedule.getDepartureStation().getCity());
        }
        if (schedule.getArrivalStation() != null) {
            dto.setArrivalStationName(schedule.getArrivalStation().getName());
            dto.setArrivalStationCity(schedule.getArrivalStation().getCity());
        }
        dto.setDepartureTime(schedule.getDepartureTime());
        dto.setArrivalTime(schedule.getArrivalTime());
        dto.setTrainName(schedule.getTrainName());
        dto.setPrice(schedule.getPrice());
        dto.setAvailableSeats(schedule.getAvailableSeats());
        return dto;
    }

    @Transactional
    public ScheduleResponseDto createSchedule(ScheduleRequestDto requestDto) {
        logger.info("Attempting to create new schedule for train: {}", requestDto.getTrainName());
        Station departureStation = stationRepository.findById(requestDto.getDepartureStationId())
                .orElseThrow(() -> {
                    logger.warn("Departure Station not found with id: {}", requestDto.getDepartureStationId());
                    return new ResourceNotFoundException("Stasiun keberangkatan tidak ditemukan dengan id: " + requestDto.getDepartureStationId());
                });
        Station arrivalStation = stationRepository.findById(requestDto.getArrivalStationId())
                .orElseThrow(() -> {
                    logger.warn("Arrival Station not found with id: {}", requestDto.getArrivalStationId());
                    return new ResourceNotFoundException("Stasiun kedatangan tidak ditemukan dengan id: " + requestDto.getArrivalStationId());
                });

        if (requestDto.getArrivalTime().isBefore(requestDto.getDepartureTime())) {
            logger.warn("Invalid schedule time: Arrival time is before departure time.");
            throw new IllegalArgumentException("Waktu kedatangan harus setelah waktu keberangkatan.");
        }
        if (requestDto.getDepartureStationId().equals(requestDto.getArrivalStationId())) {
            logger.warn("Invalid schedule: Departure and arrival stations are the same.");
            throw new IllegalArgumentException("Stasiun keberangkatan dan kedatangan tidak boleh sama.");
        }


        Schedule schedule = new Schedule();
        schedule.setDepartureStation(departureStation);
        schedule.setArrivalStation(arrivalStation);
        schedule.setDepartureTime(requestDto.getDepartureTime());
        schedule.setArrivalTime(requestDto.getArrivalTime());
        schedule.setTrainName(requestDto.getTrainName());
        schedule.setPrice(requestDto.getPrice());
        schedule.setAvailableSeats(requestDto.getAvailableSeats());

        Schedule savedSchedule = scheduleRepository.save(schedule);
        logger.info("Schedule created successfully with ID: {}", savedSchedule.getId());
        return convertToResponseDto(savedSchedule);
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> getAllSchedules() {
        logger.info("Fetching all schedules");
        return scheduleRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ScheduleResponseDto getScheduleById(Long id) {
        logger.info("Fetching schedule with ID: {}", id);
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Schedule not found with ID: {}", id);
                    return new ResourceNotFoundException("Jadwal tidak ditemukan dengan id: " + id);
                });
        return convertToResponseDto(schedule);
    }

    @Transactional
    public ScheduleResponseDto updateSchedule(Long id, ScheduleRequestDto requestDto) {
        logger.info("Attempting to update schedule with ID: {}", id);
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> {
                     logger.warn("Schedule not found for update with ID: {}", id);
                    return new ResourceNotFoundException("Jadwal tidak ditemukan dengan id: " + id + " untuk diupdate");
                });

        Station departureStation = stationRepository.findById(requestDto.getDepartureStationId())
                .orElseThrow(() -> {
                    logger.warn("Departure Station not found for update with ID: {}", requestDto.getDepartureStationId());
                    return new ResourceNotFoundException("Stasiun keberangkatan tidak ditemukan dengan id: " + requestDto.getDepartureStationId());
                });
        Station arrivalStation = stationRepository.findById(requestDto.getArrivalStationId())
                .orElseThrow(() -> {
                    logger.warn("Arrival Station not found for update with ID: {}", requestDto.getArrivalStationId());
                    return new ResourceNotFoundException("Stasiun kedatangan tidak ditemukan dengan id: " + requestDto.getArrivalStationId());
                });
        
        if (requestDto.getArrivalTime().isBefore(requestDto.getDepartureTime())) {
            logger.warn("Invalid schedule time for update: Arrival time is before departure time.");
            throw new IllegalArgumentException("Waktu kedatangan harus setelah waktu keberangkatan.");
        }
        if (requestDto.getDepartureStationId().equals(requestDto.getArrivalStationId())) {
            logger.warn("Invalid schedule for update: Departure and arrival stations are the same.");
            throw new IllegalArgumentException("Stasiun keberangkatan dan kedatangan tidak boleh sama.");
        }

        schedule.setDepartureStation(departureStation);
        schedule.setArrivalStation(arrivalStation);
        schedule.setDepartureTime(requestDto.getDepartureTime());
        schedule.setArrivalTime(requestDto.getArrivalTime());
        schedule.setTrainName(requestDto.getTrainName());
        schedule.setPrice(requestDto.getPrice());
        schedule.setAvailableSeats(requestDto.getAvailableSeats());

        Schedule updatedSchedule = scheduleRepository.save(schedule);
        logger.info("Schedule updated successfully for ID: {}", updatedSchedule.getId());
        return convertToResponseDto(updatedSchedule);
    }

    @Transactional
    public void deleteSchedule(Long id) {
        logger.info("Attempting to delete schedule with ID: {}", id);
        if (!scheduleRepository.existsById(id)) {
            logger.warn("Schedule not found for deletion with ID: {}", id);
            throw new ResourceNotFoundException("Jadwal tidak ditemukan dengan id: " + id + " untuk dihapus");
        }
        scheduleRepository.deleteById(id);
        logger.info("Schedule deleted successfully with ID: {}", id);
    }
}