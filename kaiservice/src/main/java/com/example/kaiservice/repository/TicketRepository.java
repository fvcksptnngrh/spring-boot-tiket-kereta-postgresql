package com.example.kaiservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.kaiservice.entity.Ticket;

@Repository
public interface TicketRepository extends MongoRepository<Ticket, String> { // Menggunakan String untuk ID

   // Mencari berdasarkan userId yang disimpan di dokumen Ticket
   List<Ticket> findByUserId(String userId);

   // Mencari tiket berdasarkan ID dan userId
   Optional<Ticket> findByIdAndUserId(String id, String userId);
}