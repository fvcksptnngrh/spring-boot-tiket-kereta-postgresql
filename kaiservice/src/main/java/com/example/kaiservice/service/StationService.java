package com.example.kaiservice.service;

import com.example.kaiservice.dto.StationDto;
import com.example.kaiservice.entity.Station;
import com.example.kaiservice.repository.StationRepository;
import com.example.kaiservice.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {

    @Autowired
    private StationRepository stationRepository;

    private StationDto convertToDto(Station station) {
        // ID station sekarang String
        return new StationDto(station.getId(), station.getName(), station.getCity());
    }

    private Station convertToEntity(StationDto stationDto) {
        Station station = new Station();
        // ID tidak di-set di sini, akan di-generate oleh MongoDB saat save
        station.setName(stationDto.getName());
        station.setCity(stationDto.getCity());
        return station;
    }

    @Transactional
    public StationDto createStation(StationDto stationDto) {
        Station station = convertToEntity(stationDto);
        Station savedStation = stationRepository.save(station);
        return convertToDto(savedStation);
    }

    @Transactional(readOnly = true)
    public List<StationDto> getAllStations() {
        return stationRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StationDto getStationById(String id) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stasiun tidak ditemukan dengan id: " + id));
        return convertToDto(station);
    }

    @Transactional
    public StationDto updateStation(String id, StationDto stationDto) { 
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stasiun tidak ditemukan dengan id: " + id + " untuk diupdate"));
        station.setName(stationDto.getName());
        station.setCity(stationDto.getCity());
        Station updatedStation = stationRepository.save(station);
        return convertToDto(updatedStation);
    }

    @Transactional
    public void deleteStation(String id) { 
        if (!stationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Stasiun tidak ditemukan dengan id: " + id + " untuk dihapus");
        }
        stationRepository.deleteById(id);
    }
}