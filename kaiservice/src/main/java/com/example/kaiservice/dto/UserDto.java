package com.example.kaiservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id; // Diubah dari Long ke String
    private String username;
    private String email;
    // Tambahkan field lain yang relevan dan aman untuk ditampilkan dalam daftar

    // Konstruktor manual jika tidak pakai Lombok AllArgsConstructor
    // public UserDto(String id, String username, String email) { // Diubah dari Long ke String
    //     this.id = id;
    //     this.username = username;
    //     this.email = email;
    // }
}