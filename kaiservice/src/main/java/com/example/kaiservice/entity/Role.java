// Di package yang sama dengan User.java atau package model lainnya
package com.example.kaiservice.entity; // Sesuaikan dengan package Anda

import jakarta.persistence.*; // atau javax.persistence.*

@Entity
@Table(name = "roles") // Nama tabel yang baru dibuat
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Atau Long jika id di tabel roles Anda Long

    private String name;

    // Constructor, getter, dan setter
    public Role() {}

    public Role(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}