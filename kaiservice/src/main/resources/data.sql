-- Reset sequences
ALTER SEQUENCE IF EXISTS roles_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS users_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS stations_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS schedules_id_seq RESTART WITH 1;

DELETE FROM schedules;

INSERT INTO roles (id, name) VALUES (1, 'ROLE_ADMIN')
ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name;
INSERT INTO roles (id, name) VALUES (2, 'ROLE_USER')
ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name;

-- Users (GANTI HASH PASSWORD DENGAN YANG ASLI DARI APLIKASI ANDA!)
-- Kita set ID manual agar mudah direferensi: 1 untuk admin01, 2 untuk user01
INSERT INTO users (id, username, email, password) VALUES (1, 'adinda honey', 'honey444@gmail.com', '$2a$10$EPZp4AbzTYDlpT5FVWtnKut6qYnNRNOBqywN9gvUQKOA.AxEX0rHy') -- << GANTI HASH INI
ON CONFLICT (id) DO UPDATE SET 
    username = EXCLUDED.username, 
    email = EXCLUDED.email, 
    password = EXCLUDED.password;
INSERT INTO users (id, username, email, password) VALUES (2, 'user01', 'user01@example.com','$2a$10$YDPxybo3qGsMkIMdN//5o.eHBhJkw503GoLcdSttmVlpc2SsY30pO') -- << GANTI HASH INI
ON CONFLICT (id) DO UPDATE SET 
    username = EXCLUDED.username, 
    email = EXCLUDED.email, 
    password = EXCLUDED.password;

-- User Roles (Menghubungkan user dengan roles menggunakan ID yang sudah kita tentukan)
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1) -- admin01 (user_id=1) mendapatkan ROLE_ADMIN (role_id=1)
ON CONFLICT (user_id, role_id) DO UPDATE SET role_id = EXCLUDED.role_id;
INSERT INTO user_roles (user_id, role_id) VALUES (2, 2) -- user01 (user_id=2) mendapatkan ROLE_USER (role_id=2)
ON CONFLICT (user_id, role_id) DO UPDATE SET role_id = EXCLUDED.role_id;

-- Stasiun (Menggunakan INSERT ... ON CONFLICT untuk PostgreSQL)
INSERT INTO stations (id, name, city) VALUES (1, 'Gambir', 'Jakarta')
ON CONFLICT (id) DO UPDATE SET 
    name = EXCLUDED.name, 
    city = EXCLUDED.city;
INSERT INTO stations (id, name, city) VALUES (2, 'Bandung', 'Bandung')
ON CONFLICT (id) DO UPDATE SET 
    name = EXCLUDED.name, 
    city = EXCLUDED.city;
INSERT INTO stations (id, name, city) VALUES (3, 'Yogyakarta', 'Yogyakarta')
ON CONFLICT (id) DO UPDATE SET 
    name = EXCLUDED.name, 
    city = EXCLUDED.city;
INSERT INTO stations (id, name, city) VALUES (4, 'Surabaya Gubeng', 'Surabaya')
ON CONFLICT (id) DO UPDATE SET 
    name = EXCLUDED.name, 
    city = EXCLUDED.city;
INSERT INTO stations (id, name, city) VALUES (5, 'Semarang Tawang', 'Semarang')
ON CONFLICT (id) DO UPDATE SET 
    name = EXCLUDED.name, 
    city = EXCLUDED.city;

-- Jadwal
-- Untuk jadwal, kita menggunakan sequence PostgreSQL untuk ID
-- Jika kita sudah DELETE semua data schedules di awal, ON CONFLICT mungkin tidak terlalu krusial
-- kecuali jika Anda menjalankan skrip ini tanpa DELETE sebelumnya dan ada kemungkinan ID duplikat.
INSERT INTO schedules (departure_station_id, arrival_station_id, departure_time, arrival_time, train_name, price, available_seats) VALUES
(1, 2, '2025-07-15 08:00:00', '2025-07-15 11:00:00', 'Argo Parahyangan', 150000.00, 50),
(2, 1, '2025-07-15 12:00:00', '2025-07-15 15:00:00', 'Argo Parahyangan', 150000.00, 45),
(1, 3, '2025-07-16 07:00:00', '2025-07-16 14:30:00', 'Taksaka', 450000.00, 30),
(3, 1, '2025-07-16 16:00:00', '2025-07-16 23:30:00', 'Taksaka', 450000.00, 25),
(1, 5, '2025-07-17 09:00:00', '2025-07-17 15:00:00', 'Argo Bromo Anggrek', 550000.00, 40),
(5, 1, '2025-07-17 16:00:00', '2025-07-17 22:00:00', 'Argo Bromo Anggrek', 550000.00, 35)
ON CONFLICT (id) DO UPDATE SET
    departure_station_id = EXCLUDED.departure_station_id,
    arrival_station_id = EXCLUDED.arrival_station_id,
    departure_time = EXCLUDED.departure_time,
    arrival_time = EXCLUDED.arrival_time,
    train_name = EXCLUDED.train_name,
    price = EXCLUDED.price,
    available_seats = EXCLUDED.available_seats;