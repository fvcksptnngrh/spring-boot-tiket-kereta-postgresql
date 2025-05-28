package com.example.kaiservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmbeddedStationInfo { // Tambahkan 'public'
    private String stationId;
    private String name;
    private String city;
}