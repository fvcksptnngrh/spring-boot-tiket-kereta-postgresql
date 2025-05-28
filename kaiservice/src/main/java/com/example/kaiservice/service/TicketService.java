package com.example.kaiservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kaiservice.dto.BookingRequest;
import com.example.kaiservice.dto.ScheduleResponseDto;
import com.example.kaiservice.dto.TicketDto;
import com.example.kaiservice.entity.EmbeddedScheduleSummary;
import com.example.kaiservice.entity.Schedule;
import com.example.kaiservice.entity.Ticket;
import com.example.kaiservice.entity.TicketStatus;
import com.example.kaiservice.entity.User;
import com.example.kaiservice.exception.NotEnoughSeatsException;
import com.example.kaiservice.exception.ResourceNotFoundException;
import com.example.kaiservice.repository.ScheduleRepository;
import com.example.kaiservice.repository.TicketRepository;
import com.example.kaiservice.repository.UserRepository;

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

    private TicketDto convertTicketToDto(Ticket ticket) {
        TicketDto dto = new TicketDto();
        dto.setId(ticket.getId());
        dto.setUserId(ticket.getUserId());

        if (ticket.getScheduleSummary() != null) {
            EmbeddedScheduleSummary summary = ticket.getScheduleSummary();
            ScheduleResponseDto scheduleDto = new ScheduleResponseDto();
            scheduleDto.setId(summary.getScheduleId());
            scheduleDto.setTrainName(summary.getTrainName());
            scheduleDto.setDepartureStationName(summary.getDepartureStationName());
            scheduleDto.setDepartureStationCity(summary.getDepartureStationCity());
            scheduleDto.setArrivalStationName(summary.getArrivalStationName());
            scheduleDto.setArrivalStationCity(summary.getArrivalStationCity());
            scheduleDto.setDepartureTime(summary.getDepartureTime());
            scheduleDto.setArrivalTime(summary.getArrivalTime());
            scheduleDto.setPrice(summary.getPrice()); // Ini adalah harga satuan per tiket dari jadwal
            // Anda sudah menambahkan availableSeats di EmbeddedScheduleSummary, jadi kita map juga
            scheduleDto.setAvailableSeats(summary.getAvailableSeats()); 
            dto.setSchedule(scheduleDto);
        }

        dto.setBookingTime(ticket.getBookingTime());
        dto.setSeatNumber(ticket.getSeatNumber());
        if (ticket.getStatus() != null) {
            dto.setStatus(ticket.getStatus().name());
        }

        // Petakan field baru
        dto.setPassengerCount(ticket.getPassengerCount());
        dto.setTotalPrice(ticket.getTotalPrice());

        return dto;
    }

    @Transactional
    public TicketDto bookTicket(BookingRequest bookingRequest) {
        User currentUser = getCurrentUser();
        String scheduleIdToFind = bookingRequest.getScheduleId();

        logger.info("User {} attempting to book ticket for schedule ID: {} with {} passenger(s)",
                    currentUser.getUsername(), scheduleIdToFind, bookingRequest.getPassengerCount());

        Schedule schedule = scheduleRepository.findById(scheduleIdToFind)
                .orElseThrow(() -> {
                    logger.warn("Schedule not found with ID: {}", scheduleIdToFind);
                    return new ResourceNotFoundException("Jadwal tidak ditemukan dengan ID: " + scheduleIdToFind);
                });

        if (schedule.getAvailableSeats() == null || schedule.getAvailableSeats() < bookingRequest.getPassengerCount()) {
            logger.warn("Not enough seats available for schedule ID: {}. Available: {}, Requested: {}",
                        schedule.getId(), schedule.getAvailableSeats(), bookingRequest.getPassengerCount());
            throw new NotEnoughSeatsException("Kursi untuk jadwal ini tidak mencukupi jumlah penumpang yang diminta!");
        }

        Ticket newTicket = new Ticket();
        newTicket.setUserId(currentUser.getId());

        // Isi EmbeddedScheduleSummary (sudah termasuk city dan availableSeats)
        EmbeddedScheduleSummary summary = new EmbeddedScheduleSummary();
        summary.setScheduleId(schedule.getId());
        summary.setTrainName(schedule.getTrainName());
        if (schedule.getDepartureStationInfo() != null) {
            summary.setDepartureStationName(schedule.getDepartureStationInfo().getName());
            summary.setDepartureStationCity(schedule.getDepartureStationInfo().getCity());
        }
        if (schedule.getArrivalStationInfo() != null) {
            summary.setArrivalStationName(schedule.getArrivalStationInfo().getName());
            summary.setArrivalStationCity(schedule.getArrivalStationInfo().getCity());
        }
        summary.setDepartureTime(schedule.getDepartureTime());
        summary.setArrivalTime(schedule.getArrivalTime());
        summary.setPrice(schedule.getPrice()); // Harga satuan
        summary.setAvailableSeats(schedule.getAvailableSeats()); // Kursi tersedia SEBELUM pemesanan ini
        newTicket.setScheduleSummary(summary);

        // Set field baru untuk multi-penumpang dan total harga
        newTicket.setPassengerCount(bookingRequest.getPassengerCount());
        newTicket.setTotalPrice(schedule.getPrice() * bookingRequest.getPassengerCount()); // HITUNG TOTAL HARGA

        newTicket.setBookingTime(LocalDateTime.now());
        newTicket.setStatus(TicketStatus.BOOKED);
        newTicket.setSeatNumber(bookingRequest.getSeatNumber()); // Perlu dipikirkan untuk alokasi multi-kursi

        // Kurangi kursi yang tersedia di jadwal
        schedule.setAvailableSeats(schedule.getAvailableSeats() - bookingRequest.getPassengerCount());
        scheduleRepository.save(schedule);

        Ticket savedTicket = ticketRepository.save(newTicket);
        logger.info("Ticket booked successfully with ID: {} for user: {} and schedule: {}. Passenger(s): {}, Total Price: {}",
                    savedTicket.getId(), currentUser.getUsername(), schedule.getId(), savedTicket.getPassengerCount(), savedTicket.getTotalPrice());

        return convertTicketToDto(savedTicket);
    }

    @Transactional(readOnly = true)
    public List<TicketDto> getTicketsForCurrentUser() {
        User currentUser = getCurrentUser();
        logger.info("Fetching tickets for current user: {}", currentUser.getUsername());
        List<Ticket> tickets = ticketRepository.findByUserId(currentUser.getId());
        return tickets.stream()
                      .map(this::convertTicketToDto)
                      .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TicketDto getTicketByIdForCurrentUser(String ticketId) {
        User currentUser = getCurrentUser();
        logger.info("Fetching ticket ID: {} for current user: {}", ticketId, currentUser.getUsername());

        return ticketRepository.findByIdAndUserId(ticketId, currentUser.getId())
                .map(this::convertTicketToDto)
                .orElseThrow(() -> {
                    logger.warn("Ticket ID: {} not found or does not belong to user: {}", ticketId, currentUser.getUsername());
                    return new ResourceNotFoundException("Tiket tidak ditemukan atau Anda tidak berhak mengakses tiket ini.");
                });
    }

    @Transactional(readOnly = true)
    public List<TicketDto> getAllTicketsForAdmin() {
        logger.info("Admin fetching all tickets.");
        List<Ticket> tickets = ticketRepository.findAll();
        return tickets.stream()
                .map(this::convertTicketToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TicketDto getAnyTicketByIdForAdmin(String ticketId) {
        logger.info("Admin fetching ticket by ID: {}", ticketId);
        return ticketRepository.findById(ticketId)
                .map(this::convertTicketToDto)
                .orElseThrow(() -> {
                    logger.warn("Admin: Ticket not found with ID: {}", ticketId);
                    return new ResourceNotFoundException("Tiket tidak ditemukan dengan ID: " + ticketId);
                });
    }
}