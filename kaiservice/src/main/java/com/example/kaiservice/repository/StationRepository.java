package com.example.kaiservice.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.kaiservice.entity.Station; // Tambahkan impor ini

@Repository
public interface StationRepository extends MongoRepository<Station, String> {
    // Tambahkan metode ini:
    Optional<Station> findByName(String name);
}