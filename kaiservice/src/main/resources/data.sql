-- Hapus data lama jika ada (Baris ini OK)
DELETE FROM tickets;
DELETE FROM user_profiles;
DELETE FROM schedules;
DELETE FROM stations;
DELETE FROM users;


-- MENJADI SEPERTI INI:
MERGE INTO stations (id, name, city) KEY(id) VALUES (1, 'Gambir', 'Jakarta');
MERGE INTO stations (id, name, city) KEY(id) VALUES (2, 'Bandung', 'Bandung');
MERGE INTO stations (id, name, city) KEY(id) VALUES (3, 'Yogyakarta', 'Yogyakarta');
MERGE INTO stations (id, name, city) KEY(id) VALUES (4, 'Surabaya Gubeng', 'Surabaya');
MERGE INTO stations (id, name, city) KEY(id) VALUES (5, 'Semarang Tawang', 'Semarang');

-- Lakukan perubahan serupa untuk INSERT ke tabel schedules jika Anda menggunakan ON CONFLICT di sana juga
-- Contoh:
-- MERGE INTO schedules (id, origin_station_id, ...) KEY(id) VALUES (1, 1, ...);

-- Baris INSERT schedules Anda yang asli (jika tidak pakai ON CONFLICT, biarkan saja):
INSERT INTO schedules (origin_station_id, destination_station_id, departure_time, arrival_time, train_name, price, available_seats) VALUES
(1, 2, '2025-05-01 08:00:00', '2025-05-01 11:00:00', 'Argo Parahyangan', 150000.00, 50),
(2, 1, '2025-05-01 12:00:00', '2025-05-01 15:00:00', 'Argo Parahyangan', 150000.00, 45),
(1, 3, '2025-05-02 07:00:00', '2025-05-02 14:30:00', 'Taksaka', 450000.00, 30),
(3, 1, '2025-05-02 16:00:00', '2025-05-02 23:30:00', 'Taksaka', 450000.00, 25),
(1, 5, '2025-05-03 09:00:00', '2025-05-03 15:00:00', 'Argo Bromo Anggrek', 550000.00, 40);