package com.example.kaiservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.kaiservice.dto.BookingRequest;
import com.example.kaiservice.dto.TicketDto;
import com.example.kaiservice.service.TicketService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TicketDto> bookTicket(@Valid @RequestBody BookingRequest bookingRequest) {
        TicketDto bookedTicket = ticketService.bookTicket(bookingRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookedTicket);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TicketDto>> getCurrentUserTickets() {
        List<TicketDto> tickets = ticketService.getTicketsForCurrentUser();
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{ticketId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TicketDto> getTicketById(@PathVariable String ticketId) { // ID menjadi String
        TicketDto ticketDto = ticketService.getTicketByIdForCurrentUser(ticketId);
        return ResponseEntity.ok(ticketDto);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TicketDto>> getAllTicketsForAdmin() {
        List<TicketDto> tickets = ticketService.getAllTicketsForAdmin();
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketDto> getAnyTicketByIdForAdmin(@PathVariable String id) { // ID menjadi String
        TicketDto ticket = ticketService.getAnyTicketByIdForAdmin(id);
        return ResponseEntity.ok(ticket);
    }
}