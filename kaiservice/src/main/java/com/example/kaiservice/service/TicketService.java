package com.example.kaiservice.service;

import com.example.kaiservice.dto.*; // Import semua DTO
import com.example.kaiservice.entity.*; // Import semua Entity
import com.example.kaiservice.repository.ScheduleRepository;
import com.example.kaiservice.repository.TicketRepository;
import com.example.kaiservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Penting untuk booking!

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private UserRepository userRepository;

    // --- Helper Methods ---

    // Helper method untuk mendapatkan User yang sedang login (sama seperti di UserService)
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
             throw new RuntimeException("No authenticated user found");
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
    }

    // Helper method konversi Station ke StationDto (duplikasi dari ScheduleService untuk sementara)
     private StationDto convertStationToDto(Station station) {
        if (station == null) return null;
        StationDto dto = new StationDto();
        dto.setId(station.getId());
        dto.setName(station.getName());
        dto.setCity(station.getCity());
        return dto;
    }

    // Helper method konversi Schedule ke ScheduleDto (duplikasi dari ScheduleService untuk sementara)
    private ScheduleDto convertScheduleToDto(Schedule schedule) {
        if (schedule == null) return null;
        ScheduleDto dto = new ScheduleDto();
        dto.setId(schedule.getId());
        dto.setOriginStation(convertStationToDto(schedule.getOriginStation()));
        dto.setDestinationStation(convertStationToDto(schedule.getDestinationStation()));
        dto.setDepartureTime(schedule.getDepartureTime());
        dto.setArrivalTime(schedule.getArrivalTime());
        dto.setTrainName(schedule.getTrainName());
        dto.setPrice(schedule.getPrice());
        dto.setAvailableSeats(schedule.getAvailableSeats());
        return dto;
    }


    // Helper method konversi Ticket Entity ke TicketDto
    private TicketDto convertTicketToDto(Ticket ticket) {
        TicketDto dto = new TicketDto();
        dto.setId(ticket.getId());
        dto.setUserId(ticket.getUser().getId()); // Sertakan ID user
        dto.setSchedule(convertScheduleToDto(ticket.getSchedule())); // Konversi jadwal terkait
        dto.setBookingTime(ticket.getBookingTime());
        dto.setSeatNumber(ticket.getSeatNumber());
        dto.setStatus(ticket.getStatus().name()); // Ambil nama enum sebagai String
        return dto;
    }

    // --- Service Methods ---

    // Memesan Tiket
    @Transactional // Pastikan semua operasi (cek kursi, save ticket, update schedule) dalam 1 transaksi
    public TicketDto bookTicket(BookingRequest bookingRequest) {
        User currentUser = getCurrentUser(); // 1. Dapatkan user

        // 2. Cari Jadwal
        Schedule schedule = scheduleRepository.findById(bookingRequest.getScheduleId())
                .orElseThrow(() -> new RuntimeException("Schedule not found with ID: " + bookingRequest.getScheduleId())); // Ganti dgn exception spesifik

        // 3. Cek Ketersediaan Kursi
        if (schedule.getAvailableSeats() <= 0) {
            throw new RuntimeException("No seats available for this schedule!"); // Ganti dgn exception spesifik
        }

        // --- Logika Booking ---
        // 4. Buat entitas Tiket baru
        Ticket newTicket = new Ticket();
        newTicket.setUser(currentUser);
        newTicket.setSchedule(schedule);
        newTicket.setBookingTime(LocalDateTime.now());
        newTicket.setStatus(TicketStatus.BOOKED);
        newTicket.setSeatNumber(bookingRequest.getSeatNumber()); // Ambil dari request (bisa null)

        // 5. Kurangi jumlah kursi tersedia di jadwal
        schedule.setAvailableSeats(schedule.getAvailableSeats() - 1);

        // 6. Simpan perubahan (karena @Transactional, keduanya harus berhasil)
        scheduleRepository.save(schedule); // Update jadwal
        Ticket savedTicket = ticketRepository.save(newTicket); // Simpan tiket baru

        // 7. Konversi ke DTO dan kembalikan
        return convertTicketToDto(savedTicket);
    }

    // Mendapatkan semua tiket milik user yang sedang login
    @Transactional(readOnly = true)
    public List<TicketDto> getTicketsForCurrentUser() {
        User currentUser = getCurrentUser();
        List<Ticket> tickets = ticketRepository.findByUserId(currentUser.getId());
        return tickets.stream()
                      .map(this::convertTicketToDto)
                      .collect(Collectors.toList());
    }

    // Mendapatkan detail tiket spesifik milik user yang sedang login
    @Transactional(readOnly = true)
    public Optional<TicketDto> getTicketByIdForCurrentUser(Long ticketId) {
        User currentUser = getCurrentUser();
        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);

        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            // Verifikasi kepemilikan tiket
            if (ticket.getUser().getId().equals(currentUser.getId())) {
                return Optional.of(convertTicketToDto(ticket)); // Kembalikan DTO jika milik user
            } else {
                // Jika tiket ada tapi bukan milik user, bisa throw Forbidden atau return empty
                return Optional.empty(); // Anggap saja tidak ditemukan
            }
        } else {
            return Optional.empty(); // Tiket tidak ditemukan
        }
    }
}