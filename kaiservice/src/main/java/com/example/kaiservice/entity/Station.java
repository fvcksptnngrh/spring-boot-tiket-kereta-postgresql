package com.example.kaiservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "stations")
public class Station {
    @Id
    private String id;

    @Indexed(unique = true) // Nama stasiun sebaiknya unik
    private String name;

    private String city;
}