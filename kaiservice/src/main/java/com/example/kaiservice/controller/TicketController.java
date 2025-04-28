package com.example.kaiservice.controller;

import com.example.kaiservice.dto.BookingRequest;
import com.example.kaiservice.dto.MessageResponse; // Import MessageResponse
import com.example.kaiservice.dto.TicketDto;
import com.example.kaiservice.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Import HttpStatus
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Opsional
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tickets") // Base path untuk endpoint tiket
public class TicketController {

    @Autowired
    private TicketService ticketService;

    // Endpoint untuk memesan tiket
    @PostMapping
    // @PreAuthorize("isAuthenticated()") // Pastikan user login
    public ResponseEntity<?> bookTicket(@RequestBody BookingRequest bookingRequest) {
        try {
            TicketDto bookedTicket = ticketService.bookTicket(bookingRequest);
            // Kembalikan 201 Created dengan detail tiket jika sukses
            return ResponseEntity.status(HttpStatus.CREATED).body(bookedTicket);
        } catch (RuntimeException e) {
            // Tangani error (misal: Jadwal tidak ditemukan, Kursi habis)
            // Bisa dibuat lebih spesifik berdasarkan jenis exception
            return ResponseEntity.badRequest().body(new MessageResponse("Error booking ticket: " + e.getMessage()));
        }
    }

    // Endpoint untuk mendapatkan semua tiket milik user yang login
    @GetMapping
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TicketDto>> getCurrentUserTickets() {
        List<TicketDto> tickets = ticketService.getTicketsForCurrentUser();
        return ResponseEntity.ok(tickets); // Kembalikan daftar tiket (bisa kosong)
    }

    // Endpoint untuk mendapatkan detail tiket spesifik
    @GetMapping("/{ticketId}")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TicketDto> getTicketById(@PathVariable Long ticketId) {
        Optional<TicketDto> ticketDtoOpt = ticketService.getTicketByIdForCurrentUser(ticketId);

        // Jika tiket ditemukan dan milik user, kembalikan OK (200)
        // Jika tidak (tidak ditemukan atau bukan milik user), kembalikan Not Found (404)
        return ticketDtoOpt.map(ResponseEntity::ok)
                           .orElseGet(() -> ResponseEntity.notFound().build());
    }
}