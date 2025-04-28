package com.example.kaiservice.service;

import com.example.kaiservice.dto.ScheduleDto;
import com.example.kaiservice.dto.SeatDto;
import com.example.kaiservice.dto.StationDto;
import com.example.kaiservice.entity.Schedule;
import com.example.kaiservice.entity.Station; // Import Station entity
import com.example.kaiservice.repository.ScheduleRepository;
import com.example.kaiservice.repository.StationRepository; // Kita mungkin perlu ini jika filter pakai ID
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import Transactional

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList; // Import ArrayList
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    // Service untuk konversi Station ke DTO (atau bisa inject StationService)
    // Untuk simpel, kita buat method konversi di sini
    private StationDto convertStationToDto(Station station) {
        if (station == null) return null;
        StationDto dto = new StationDto();
        dto.setId(station.getId());
        dto.setName(station.getName());
        dto.setCity(station.getCity());
        return dto;
    }

    // Method utama untuk konversi Schedule Entity ke ScheduleDto
    private ScheduleDto convertToDto(Schedule schedule) {
        ScheduleDto dto = new ScheduleDto();
        dto.setId(schedule.getId());
        // Konversi Station asal dan tujuan
        dto.setOriginStation(convertStationToDto(schedule.getOriginStation()));
        dto.setDestinationStation(convertStationToDto(schedule.getDestinationStation()));
        dto.setDepartureTime(schedule.getDepartureTime());
        dto.setArrivalTime(schedule.getArrivalTime());
        dto.setTrainName(schedule.getTrainName());
        dto.setPrice(schedule.getPrice());
        dto.setAvailableSeats(schedule.getAvailableSeats()); // Sertakan jumlah kursi tersedia
        return dto;
    }

    // Mengambil semua jadwal atau memfilter berdasarkan kriteria
    @Transactional(readOnly = true) // Baik untuk operasi read, pastikan relasi ter-load jika LAZY
    public List<ScheduleDto> findSchedules(Long originStationId, Long destinationStationId, LocalDate departureDate) {
        List<Schedule> schedules;

        if (originStationId != null && destinationStationId != null && departureDate != null) {
            // Jika semua filter ada: cari berdasarkan asal, tujuan, dan tanggal
            LocalDateTime startOfDay = departureDate.atStartOfDay(); // 00:00:00
            LocalDateTime endOfDay = departureDate.atTime(LocalTime.MAX);   // 23:59:59.999...
            schedules = scheduleRepository.findByOriginStationIdAndDestinationStationIdAndDepartureTimeBetween(
                    originStationId, destinationStationId, startOfDay, endOfDay);
        } else if (departureDate != null) {
             // Jika hanya tanggal: cari semua jadwal setelah tanggal tersebut (atau pada tanggal tsb)
             LocalDateTime startOfDay = departureDate.atStartOfDay();
             schedules = scheduleRepository.findByDepartureTimeAfter(startOfDay); // Sesuaikan query jika perlu
        }
         else {
            // Jika tidak ada filter / filter tidak lengkap: ambil semua jadwal
            schedules = scheduleRepository.findAll();
            // Atau bisa juga filter hanya berdasarkan asal/tujuan jika ada
        }

        // Konversi List<Schedule> ke List<ScheduleDto>
        return schedules.stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());
    }


    // Mengambil detail satu jadwal berdasarkan ID
    @Transactional(readOnly = true)
    public Optional<ScheduleDto> getScheduleById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId) // Mengembalikan Optional<Schedule>
                                 .map(this::convertToDto); // Konversi jika ada, tetap Optional kosong jika tidak
    }

    // Mengambil daftar kursi (implementasi sederhana/dummy)
    @Transactional(readOnly = true)
    public Optional<List<SeatDto>> getAvailableSeats(Long scheduleId) {
        // 1. Cari dulu schedulenya
        Optional<Schedule> scheduleOpt = scheduleRepository.findById(scheduleId);

        if (!scheduleOpt.isPresent()) {
            return Optional.empty(); // Jadwal tidak ditemukan
        }

        // Schedule schedule = scheduleOpt.get();
        // 2. Implementasi Logika Kursi (Untuk sekarang kita buat dummy)
        //    Di aplikasi nyata: Anda akan query ke tabel 'Seats' yang berelasi dengan Schedule ini.
        List<SeatDto> seats = new ArrayList<>();
        // Misal kita buat 10 kursi dummy
        for (int i = 1; i <= 10; i++) {
            // Status bisa dibuat random atau selalu available untuk contoh ini
            String status = (Math.random() < 0.7) ? "AVAILABLE" : "BOOKED"; // 70% available
            seats.add(new SeatDto("A" + i, status));
            seats.add(new SeatDto("B" + i, "AVAILABLE")); // Baris B selalu available
        }
        // Anda bisa sesuaikan jumlah kursi dummy sesuai schedule.getAvailableSeats()
        // Atau implementasi query ke database kursi yang sebenarnya.

        return Optional.of(seats); // Kembalikan daftar kursi dummy
    }

     // Method untuk menambahkan data jadwal awal (jika diperlukan, panggil dari CommandLineRunner misal)
     @Transactional
     public void addInitialSchedules() {
         // Pastikan ada data stasiun dulu di DB
         // Anda mungkin perlu inject StationRepository di sini untuk mengambil stasiun
         // Station gambir = stationRepository.findByName("Gambir").orElse(null);
         // Station bandung = stationRepository.findByName("Bandung").orElse(null);
         // if (gambir != null && bandung != null) {
         //     Schedule schedule1 = new Schedule(null, gambir, bandung, LocalDateTime.now().plusDays(1).withHour(8).withMinute(0), LocalDateTime.now().plusDays(1).withHour(11).withMinute(0), "Argo Parahyangan", new BigDecimal("150000.00"), 50);
         //     Schedule schedule2 = new Schedule(null, bandung, gambir, LocalDateTime.now().plusDays(1).withHour(12).withMinute(0), LocalDateTime.now().plusDays(1).withHour(15).withMinute(0), "Argo Parahyangan", new BigDecimal("150000.00"), 50);
         //     scheduleRepository.save(schedule1);
         //     scheduleRepository.save(schedule2);
         // }
         System.out.println("Initial schedule logic needs station data to be present.");
     }
}