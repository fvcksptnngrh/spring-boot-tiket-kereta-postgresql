package com.example.kaiservice.controller;

import com.example.kaiservice.dto.BookingRequest;
import com.example.kaiservice.dto.TicketDto;
import com.example.kaiservice.service.TicketService;
import jakarta.validation.Valid; // Import untuk @Valid
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Import untuk @PreAuthorize
import org.springframework.web.bind.annotation.*;

import java.util.List;
// Optional tidak perlu di-import di sini jika service mengembalikan DTO langsung atau exception

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    // Endpoint untuk memesan tiket
    @PostMapping
    @PreAuthorize("isAuthenticated()") // Aktifkan kembali! Semua user yang login bisa pesan.
    public ResponseEntity<TicketDto> bookTicket(@Valid @RequestBody BookingRequest bookingRequest) {
        // try-catch block di sini bisa dihilangkan jika GlobalExceptionHandler sudah menangani
        // exception dari service (seperti ResourceNotFoundException, NotEnoughSeatsException)
        TicketDto bookedTicket = ticketService.bookTicket(bookingRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookedTicket);
    }

    // Endpoint untuk mendapatkan semua tiket milik user yang login
    @GetMapping
    @PreAuthorize("isAuthenticated()") // Aktifkan kembali!
    public ResponseEntity<List<TicketDto>> getCurrentUserTickets() {
        List<TicketDto> tickets = ticketService.getTicketsForCurrentUser();
        return ResponseEntity.ok(tickets);
    }

    // Endpoint untuk mendapatkan detail tiket spesifik milik user yang login
    @GetMapping("/{ticketId}")
    @PreAuthorize("isAuthenticated()") // Aktifkan kembali!
    public ResponseEntity<TicketDto> getTicketById(@PathVariable Long ticketId) {
        // Service akan melempar ResourceNotFoundException jika tiket tidak ditemukan atau bukan milik user
        TicketDto ticketDto = ticketService.getTicketByIdForCurrentUser(ticketId);
        return ResponseEntity.ok(ticketDto);
    }

    // --- Endpoint untuk ADMIN yang sudah kita diskusikan sebelumnya ---
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TicketDto>> getAllTicketsForAdmin() {
        List<TicketDto> tickets = ticketService.getAllTicketsForAdmin();
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketDto> getAnyTicketByIdForAdmin(@PathVariable Long id) {
        TicketDto ticket = ticketService.getAnyTicketByIdForAdmin(id);
        return ResponseEntity.ok(ticket);
    }
}