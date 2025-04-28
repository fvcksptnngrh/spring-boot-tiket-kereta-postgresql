package com.example.kaiservice.service;

import com.example.kaiservice.dto.StationDto;
import com.example.kaiservice.entity.Station;
import com.example.kaiservice.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {

    @Autowired
    private StationRepository stationRepository;

    // Method untuk mengambil semua stasiun
    public List<StationDto> getAllStations() {
        List<Station> stations = stationRepository.findAll(); // Ambil semua data dari DB
        // Konversi List<Station> menjadi List<StationDto>
        return stations.stream()
                       .map(this::convertToDto) // Panggil method convertToDto untuk setiap elemen
                       .collect(Collectors.toList()); // Kumpulkan hasilnya menjadi List baru
    }

    // Helper method untuk konversi Entity Station ke StationDto
    private StationDto convertToDto(Station station) {
        StationDto dto = new StationDto();
        dto.setId(station.getId());
        dto.setName(station.getName());
        dto.setCity(station.getCity());
        return dto;
    }

    // Opsional: Jika perlu menambahkan stasiun baru via API (tapi tidak diminta)
    // public StationDto addStation(StationDto stationDto) {
    //     Station station = new Station();
    //     station.setName(stationDto.getName());
    //     station.setCity(stationDto.getCity());
    //     Station savedStation = stationRepository.save(station);
    //     return convertToDto(savedStation);
    // }
}