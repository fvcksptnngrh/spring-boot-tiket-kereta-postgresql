package com.example.kaiservice.repository;

import com.example.kaiservice.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.kaiservice.entity.User;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

   List<Ticket> findByUser(User user); // Untuk getTicketsForCurrentUser

    Optional<Ticket> findByIdAndUser(Long id, User user);  // Cari semua tiket milik user tertentu
}