package com.example.kaiservice.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tickets")
public class Ticket {
    @Id
    private String id;

    @Field("userId")
    private String userId;

    private EmbeddedScheduleSummary scheduleSummary;

    private LocalDateTime bookingTime;
    private String seatNumber;
    private TicketStatus status;

    private Integer passengerCount; // TAMBAHKAN INI
    private Double totalPrice;      // TAMBAHKAN INI
}