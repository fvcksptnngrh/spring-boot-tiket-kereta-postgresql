package com.example.kaiservice.dto;

import lombok.Data;
import java.time.LocalDateTime;
// Jangan lupa import ScheduleResponseDto jika belum
// import com.example.kaiservice.dto.ScheduleResponseDto; // Import jika diperlukan

@Data
public class TicketDto {
    private Long id;
    private Long userId;
    // private String username; // Anda bisa tambahkan ini jika perlu

    // GANTI TIPE DI SINI JIKA PERLU
    private ScheduleResponseDto schedule; // Gunakan DTO yang benar untuk jadwal
                                       // Jika Anda membuat ScheduleDto (bukan ScheduleResponseDto), gunakan itu.

    private LocalDateTime bookingTime;
    private String seatNumber;
    private String status; // Tampilan status sebagai String

    // Anda bisa menambahkan getter/setter manual jika tidak semua field ingin di-cover @Data
    // atau jika Anda melakukan transformasi khusus
    // public ScheduleResponseDto getSchedule() {
    //     return schedule;
    // }

    // public void setSchedule(ScheduleResponseDto schedule) {
    //     this.schedule = schedule;
    // }
}