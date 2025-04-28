package com.example.kaiservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Relasi ke User yang memesan
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) // Relasi ke Jadwal yang dipesan
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Column(nullable = false)
    private LocalDateTime bookingTime;

    private String seatNumber; // Nomor kursi yang dipilih (bisa null jika belum dipilih)

    @Enumerated(EnumType.STRING) // Menyimpan enum sebagai String di DB
    private TicketStatus status;

    // Bisa tambahkan detail penumpang jika 1 tiket bisa multi-penumpang
    // private String passengerName;
}