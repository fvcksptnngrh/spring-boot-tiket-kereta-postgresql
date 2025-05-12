package com.example.kaiservice.service;

// Import DTOs secara spesifik
import com.example.kaiservice.dto.BookingRequest;
import com.example.kaiservice.dto.ScheduleResponseDto; // Pastikan ini adalah nama DTO respons jadwal Anda
import com.example.kaiservice.dto.StationDto;
import com.example.kaiservice.dto.TicketDto;

// Import Entities secara spesifik
import com.example.kaiservice.entity.Schedule;
import com.example.kaiservice.entity.Station;
import com.example.kaiservice.entity.Ticket;
import com.example.kaiservice.entity.User;
import com.example.kaiservice.entity.TicketStatus; // Pastikan Enum ini ada di package entity

// Import Repositories
import com.example.kaiservice.repository.ScheduleRepository;
import com.example.kaiservice.repository.TicketRepository;
import com.example.kaiservice.repository.UserRepository;

// Import Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Import Spring & Java lainnya
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private UserRepository userRepository;

    // --- Helper Methods ---

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            logger.warn("Attempted to get current user but no authentication found or user is anonymous.");
            throw new RuntimeException("No authenticated user found. Please login.");
        }
        String username = authentication.getName();
        logger.debug("Fetching current user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User Not Found in DB after successful authentication: {}", username);
                    return new UsernameNotFoundException("User Not Found with username: " + username);
                });
    }

    // Method ini bisa Anda hapus jika tidak terpakai di kelas ini, untuk mengurangi warning
    // private StationDto convertStationToDto(Station station) {
    //     if (station == null) return null;
    //     StationDto dto = new StationDto();
    //     dto.setId(station.getId());
    //     dto.setName(station.getName());
    //     dto.setCity(station.getCity());
    //     return dto;
    // }

    private ScheduleResponseDto convertScheduleToDto(Schedule schedule) {
        if (schedule == null) return null;
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


    private TicketDto convertTicketToDto(Ticket ticket) {
        TicketDto dto = new TicketDto();
        dto.setId(ticket.getId());
        if (ticket.getUser() != null) {
            dto.setUserId(ticket.getUser().getId());
        }
        if (ticket.getSchedule() != null) {
            dto.setSchedule(convertScheduleToDto(ticket.getSchedule()));
        }
        dto.setBookingTime(ticket.getBookingTime());
        dto.setSeatNumber(ticket.getSeatNumber());
        if (ticket.getStatus() != null) {
            dto.setStatus(ticket.getStatus().name());
        }
        return dto;
    }

    // --- Service Methods ---

    @Transactional
    public TicketDto bookTicket(BookingRequest bookingRequest) {
        User currentUser = getCurrentUser();
        logger.info("User {} attempting to book ticket for schedule ID: {}", currentUser.getUsername(), bookingRequest.getScheduleId());

        Schedule schedule = scheduleRepository.findById(bookingRequest.getScheduleId())
                .orElseThrow(() -> {
                    logger.warn("Schedule not found with ID: {}", bookingRequest.getScheduleId());
                    return new RuntimeException("Schedule not found with ID: " + bookingRequest.getScheduleId());
                });

        if (schedule.getAvailableSeats() == null || schedule.getAvailableSeats() <= 0) {
            logger.warn("No seats available for schedule ID: {}. Available: {}", schedule.getId(), schedule.getAvailableSeats());
            throw new RuntimeException("No seats available for this schedule!");
        }

        Ticket newTicket = new Ticket();
        newTicket.setUser(currentUser);
        newTicket.setSchedule(schedule);
        newTicket.setBookingTime(LocalDateTime.now());
        newTicket.setStatus(TicketStatus.BOOKED);
        newTicket.setSeatNumber(bookingRequest.getSeatNumber());

        schedule.setAvailableSeats(schedule.getAvailableSeats() - 1);

        scheduleRepository.save(schedule);
        Ticket savedTicket = ticketRepository.save(newTicket);
        logger.info("Ticket booked successfully with ID: {} for user: {} and schedule: {}", savedTicket.getId(), currentUser.getUsername(), schedule.getId());

        return convertTicketToDto(savedTicket);
    }

    @Transactional(readOnly = true)
    public List<TicketDto> getTicketsForCurrentUser() {
        User currentUser = getCurrentUser();
        logger.info("Fetching tickets for current user: {}", currentUser.getUsername());
        List<Ticket> tickets = ticketRepository.findByUser(currentUser); // Memanggil dari instance
        return tickets.stream()
                      .map(this::convertTicketToDto)
                      .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<TicketDto> getTicketByIdForCurrentUser(Long ticketId) {
        User currentUser = getCurrentUser();
        logger.info("Fetching ticket ID: {} for current user: {}", ticketId, currentUser.getUsername());

        // --- PERBAIKAN UTAMA DI SINI ---
        // 1. Hapus baris: List<Ticket> findByUser(User user);
        // 2. Pastikan TicketRepository memiliki method findByIdAndUser(Long id, User user)
        Optional<Ticket> ticketOpt = ticketRepository.findByIdAndUser(ticketId, currentUser);
        // --------------------------------

        if(ticketOpt.isPresent()){
            // Karena findByIdAndUser sudah memastikan tiket milik user, tidak perlu cek ulang kepemilikan di sini.
            logger.info("Ticket ID: {} found and belongs to user: {}. Returning DTO.", ticketId, currentUser.getUsername());
            return ticketOpt.map(this::convertTicketToDto);
        } else {
            logger.warn("Ticket ID: {} not found or does not belong to user: {}", ticketId, currentUser.getUsername());
            return Optional.empty();
        }
    }

    // --- Metode untuk ADMIN (Contoh Tambahan) ---
    @Transactional(readOnly = true)
    public List<TicketDto> getAllTicketsForAdmin() {
        logger.info("Admin fetching all tickets.");
        List<Ticket> tickets = ticketRepository.findAll();
        return tickets.stream()
                .map(this::convertTicketToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<TicketDto> getAnyTicketByIdForAdmin(Long ticketId) {
        logger.info("Admin fetching ticket by ID: {}", ticketId);
        return ticketRepository.findById(ticketId).map(this::convertTicketToDto);
    }
}