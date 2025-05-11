package com.example.kaiservice.dto;

// Anda bisa pakai Lombok juga jika mau
// import lombok.Data;
// import lombok.AllArgsConstructor;
// import lombok.NoArgsConstructor;

// @Data
// @NoArgsConstructor
// @AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    // Tambahkan field lain yang relevan dan aman untuk ditampilkan dalam daftar

    // Konstruktor manual jika tidak pakai Lombok AllArgsConstructor
    public UserDto(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    // Getter dan Setter manual jika tidak pakai Lombok @Data
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}