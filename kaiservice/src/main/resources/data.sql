-- Hapus data dengan urutan yang aman (dari child ke parent jika ada constraint)
-- Ini berguna jika Anda menjalankan skrip ini berulang kali pada database yang sama
DELETE FROM tickets;
DELETE FROM user_profiles;
DELETE FROM user_roles; -- Pastikan tabel ini ada sebelum tabel users dan roles didelete
DELETE FROM schedules;
DELETE FROM stations;
DELETE FROM users;      -- Hapus users sebelum roles jika ada FK dari user_roles ke users
DELETE FROM roles;      -- Hapus roles setelah users (atau setelah user_roles)

-- Roles (Kita set ID manual agar mudah direferensi: 1 untuk ADMIN, 2 untuk USER)
INSERT INTO roles (id, name) VALUES (1, 'ROLE_ADMIN')
ON DUPLICATE KEY UPDATE name=VALUES(name);
INSERT INTO roles (id, name) VALUES (2, 'ROLE_USER')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Users (GANTI HASH PASSWORD DENGAN YANG ASLI DARI APLIKASI ANDA!)
-- Kita set ID manual agar mudah direferensi: 1 untuk admin01, 2 untuk user01
INSERT INTO users (id, username, email, password) VALUES (1, 'adinda honey', 'honey444@gmail.com', '$2a$10$EPZp4AbzTYDlpT5FVWtnKut6qYnNRNOBqywN9gvUQKOA.AxEX0rHy') -- << GANTI HASH INI
ON DUPLICATE KEY UPDATE username=VALUES(username), email=VALUES(email), password=VALUES(password);
INSERT INTO users (id, username, email, password) VALUES (5, 'user01', 'user01@example.com','$2a$10$YDPxybo3qGsMkIMdN//5o.eHBhJkw503GoLcdSttmVlpc2SsY30pO') -- << GANTI HASH INI
ON DUPLICATE KEY UPDATE username=VALUES(username), email=VALUES(email), password=VALUES(password);

-- User Roles (Menghubungkan user dengan roles menggunakan ID yang sudah kita tentukan)
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1) -- admin01 (user_id=1) mendapatkan ROLE_ADMIN (role_id=1)
ON DUPLICATE KEY UPDATE role_id=VALUES(role_id); -- Atau bisa diabaikan jika PK (user_id, role_id)
INSERT INTO user_roles (user_id, role_id) VALUES (2, 2) -- user01 (user_id=2) mendapatkan ROLE_USER (role_id=2)
ON DUPLICATE KEY UPDATE role_id=VALUES(role_id);

-- Stasiun (Menggunakan INSERT ... ON DUPLICATE KEY UPDATE untuk MySQL)
INSERT INTO stations (id, name, city) VALUES (1, 'Gambir', 'Jakarta')
ON DUPLICATE KEY UPDATE name=VALUES(name), city=VALUES(city);
INSERT INTO stations (id, name, city) VALUES (2, 'Bandung', 'Bandung')
ON DUPLICATE KEY UPDATE name=VALUES(name), city=VALUES(city);
INSERT INTO stations (id, name, city) VALUES (3, 'Yogyakarta', 'Yogyakarta')
ON DUPLICATE KEY UPDATE name=VALUES(name), city=VALUES(city);
INSERT INTO stations (id, name, city) VALUES (4, 'Surabaya Gubeng', 'Surabaya')
ON DUPLICATE KEY UPDATE name=VALUES(name), city=VALUES(city);
INSERT INTO stations (id, name, city) VALUES (5, 'Semarang Tawang', 'Semarang')
ON DUPLICATE KEY UPDATE name=VALUES(name), city=VALUES(city);

-- Jadwal
-- Untuk jadwal, jika ID-nya auto_increment di MySQL, kita tidak perlu set kolom 'id' secara manual.
-- Dan jika kita sudah DELETE semua data schedules di awal, ON DUPLICATE KEY UPDATE mungkin tidak terlalu krusial
-- kecuali jika Anda menjalankan skrip ini tanpa DELETE sebelumnya dan ada kemungkinan ID duplikat.
-- Untuk sederhana, kita pakai INSERT biasa, ID akan auto-generated.
INSERT INTO schedules (origin_station_id, destination_station_id, departure_time, arrival_time, train_name, price, available_seats) VALUES
(1, 2, '2025-07-15 08:00:00', '2025-07-15 11:00:00', 'Argo Parahyangan', 150000.00, 50),
(2, 1, '2025-07-15 12:00:00', '2025-07-15 15:00:00', 'Argo Parahyangan', 150000.00, 45),
(1, 3, '2025-07-16 07:00:00', '2025-07-16 14:30:00', 'Taksaka', 450000.00, 30),
(3, 1, '2025-07-16 16:00:00', '2025-07-16 23:30:00', 'Taksaka', 450000.00, 25),
(1, 5, '2025-07-17 09:00:00', '2025-07-17 15:00:00', 'Argo Bromo Anggrek', 550000.00, 40);