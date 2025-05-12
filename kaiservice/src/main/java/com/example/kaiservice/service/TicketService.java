package com.example.kaiservice.service;

import com.example.kaiservice.dto.BookingRequest;
import com.example.kaiservice.dto.ScheduleResponseDto;
import com.example.kaiservice.dto.TicketDto;
import com.example.kaiservice.entity.*; // Wildcard untuk entitas, bisa juga spesifik
import com.example.kaiservice.exception.NotEnoughSeatsException; // IMPORT CUSTOM EXCEPTION
import com.example.kaiservice.exception.ResourceNotFoundException; // IMPORT CUSTOM EXCEPTION
import com.example.kaiservice.repository.ScheduleRepository;
import com.example.kaiservice.repository.TicketRepository;
import com.example.kaiservice.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            logger.warn("Attempted to get current user but no authentication found or user is anonymous.");
            // Sebaiknya dilempar sebagai AuthenticationCredentialsNotFoundException atau sejenisnya,
            // tapi RuntimeException akan ditangkap oleh GlobalExceptionHandler jika ada handler umumnya.
            // Atau biarkan AuthEntryPointJwt yang menangani jika SecurityContext kosong.
            // Untuk kasus ini, jika @PreAuthorize("isAuthenticated()") sudah ada di controller,
            // seharusnya request tidak sampai sini jika user tidak terotentikasi.
            // Jika sampai sini, berarti ada masalah di konfigurasi security atau alur.
            // Kita lempar UsernameNotFoundException agar lebih jelas konteksnya jika username tidak ada.
             String usernameForError = (authentication != null) ? authentication.getName() : "null";
            throw new UsernameNotFoundException("Authenticated user principal not found or invalid: " + usernameForError);
        }
        String username = authentication.getName();
        logger.debug("Fetching current user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User Not Found in DB after successful authentication (principal name): {}", username);
                    return new UsernameNotFoundException("User Not Found with username: " + username);
                });
    }

    // Method convertStationToDto bisa dihapus jika tidak dipakai di sini
    // private StationDto convertStationToDto(Station station) { ... }

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

    @Transactional
    public TicketDto bookTicket(BookingRequest bookingRequest) {
        User currentUser = getCurrentUser();
        logger.info("User {} attempting to book ticket for schedule ID: {}", currentUser.getUsername(), bookingRequest.getScheduleId());

        Schedule schedule = scheduleRepository.findById(bookingRequest.getScheduleId())
                .orElseThrow(() -> {
                    logger.warn("Schedule not found with ID: {}", bookingRequest.getScheduleId());
                    return new ResourceNotFoundException("Jadwal tidak ditemukan dengan ID: " + bookingRequest.getScheduleId());
                });

        if (schedule.getAvailableSeats() == null || schedule.getAvailableSeats() <= 0) {
            logger.warn("No seats available for schedule ID: {}. Available: {}", schedule.getId(), schedule.getAvailableSeats());
            throw new NotEnoughSeatsException("Kursi untuk jadwal ini sudah habis!"); // Gunakan custom exception
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
        List<Ticket> tickets = ticketRepository.findByUser(currentUser);
        return tickets.stream()
                      .map(this::convertTicketToDto)
                      .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TicketDto getTicketByIdForCurrentUser(Long ticketId) { // Mengembalikan DTO langsung atau throw exception
        User currentUser = getCurrentUser();
        logger.info("Fetching ticket ID: {} for current user: {}", ticketId, currentUser.getUsername());

        // Asumsi findByIdAndUser ada di TicketRepository dan mengembalikan Optional<Ticket>
        return ticketRepository.findByIdAndUser(ticketId, currentUser)
                .map(this::convertTicketToDto) // Jika ada, konversi ke DTO
                .orElseThrow(() -> { // Jika kosong (tidak ditemukan atau bukan milik user)
                    logger.warn("Ticket ID: {} not found or does not belong to user: {}", ticketId, currentUser.getUsername());
                    return new ResourceNotFoundException("Tiket tidak ditemukan atau Anda tidak berhak mengakses tiket ini.");
                });
    }

    // --- Metode untuk ADMIN ---
    @Transactional(readOnly = true)
    public List<TicketDto> getAllTicketsForAdmin() {
        logger.info("Admin fetching all tickets.");
        List<Ticket> tickets = ticketRepository.findAll();
        return tickets.stream()
                .map(this::convertTicketToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TicketDto getAnyTicketByIdForAdmin(Long ticketId) { // Mengembalikan DTO langsung atau throw exception
        logger.info("Admin fetching ticket by ID: {}", ticketId);
        return ticketRepository.findById(ticketId)
                .map(this::convertTicketToDto)
                .orElseThrow(() -> {
                    logger.warn("Admin: Ticket not found with ID: {}", ticketId);
                    return new ResourceNotFoundException("Tiket tidak ditemukan dengan ID: " + ticketId);
                });
    }
}