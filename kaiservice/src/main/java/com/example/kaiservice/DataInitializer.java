package com.example.kaiservice;

import java.time.LocalDateTime;
import java.util.Optional; // Tambahkan jika Anda menggunakan List di kemudian hari
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.kaiservice.entity.EmbeddedStationInfo;
import com.example.kaiservice.entity.Role;
import com.example.kaiservice.entity.Schedule;
import com.example.kaiservice.entity.Station;
import com.example.kaiservice.entity.User;
import com.example.kaiservice.repository.RoleRepository;
import com.example.kaiservice.repository.ScheduleRepository;
import com.example.kaiservice.repository.StationRepository;
import com.example.kaiservice.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Memulai inisialisasi data awal...");

        // 1. Inisialisasi Roles
        Role roleUser = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
            logger.info("ROLE_USER tidak ditemukan, membuat baru...");
            return roleRepository.save(new Role("ROLE_USER"));
        });
        logger.info("ROLE_USER ID: {}", roleUser.getId());

        Role roleAdmin = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> {
            logger.info("ROLE_ADMIN tidak ditemukan, membuat baru...");
            return roleRepository.save(new Role("ROLE_ADMIN"));
        });
        logger.info("ROLE_ADMIN ID: {}", roleAdmin.getId());

        // 2. Inisialisasi Users
        if (userRepository.findByUsername("adinda honey").isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername("adinda honey");
            adminUser.setEmail("honey444@gmail.com");
            adminUser.setPassword(passwordEncoder.encode("passwordadmin"));
            adminUser.setRoles(Set.of(roleAdmin));
            userRepository.save(adminUser);
            logger.info("Pengguna admin 'adinda honey' dibuat.");
        } else {
            logger.info("Pengguna admin 'adinda honey' sudah ada.");
        }

        if (userRepository.findByUsername("user01").isEmpty()) {
            User regularUser = new User();
            regularUser.setUsername("user01");
            regularUser.setEmail("user01@example.com");
            regularUser.setPassword(passwordEncoder.encode("passworduser"));
            regularUser.setRoles(Set.of(roleUser));
            userRepository.save(regularUser);
            logger.info("Pengguna 'user01' dibuat.");
        } else {
            logger.info("Pengguna 'user01' sudah ada.");
        }

        // 3. Inisialisasi Stasiun (menggunakan nama kanonikal)
        Station gambir = initializeStation("Gambir", "Jakarta");
        Station kiaracondong = initializeStation("Kiaracondong", "Bandung");
        Station lempuyangan = initializeStation("Lempuyangan", "Yogyakarta");
        Station surabayaGubeng = initializeStation("Surabaya Gubeng", "Surabaya");
        Station semarangTawang = initializeStation("Semarang Tawang", "Semarang");
        Station cirebonPrujakan = initializeStation("Cirebon Prujakan", "Cirebon");
        Station ancol = initializeStation("Ancol Taman Impian", "Jakarta Utara");
        Station angke = initializeStation("Angke", "DKI Jakarta");

        // 4. Inisialisasi Jadwal (dengan logika update)
        initializeSchedule(
            gambir, kiaracondong, "Argo Parahyangan",
            "2025-07-15T08:00:00", "2025-07-15T11:00:00",
            160000.00, 50 // Anda bisa mengubah harga atau kursi di sini untuk tes update
        );

        initializeSchedule(
            lempuyangan, surabayaGubeng, "Sancaka",
            "2025-07-16T07:30:00", "2025-07-16T11:30:00",
            250000.00, 40
        );

        initializeSchedule(
            surabayaGubeng, gambir, "Argo Bromo Anggrek",
            "2025-07-18T09:00:00", "2025-07-18T18:30:00",
            650000.0, 55
        );

        initializeSchedule(
            gambir, ancol, "Pantai Express",
            "2025-08-10T14:00:00", "2025-08-10T14:30:00",
            25000.0, 0 // Mungkin Anda ingin mengubah ini ke nilai yang lebih tinggi
        );

        initializeSchedule(
            semarangTawang, gambir, "Argo Muria",
            "2025-09-01T07:00:00", "2025-09-01T13:00:00",
            350000.00, 60
        );

        initializeSchedule(
            cirebonPrujakan, kiaracondong, "Ciremai Ekspres",
            "2025-09-02T10:00:00", "2025-09-02T13:30:00",
            120000.00, 70
        );
        
        initializeSchedule(
            angke, lempuyangan, "Progo",
            "2025-09-03T11:00:00", "2025-09-03T19:00:00",
            180000.00, 75
        );

        logger.info("Inisialisasi data awal selesai.");
    }

    private Station initializeStation(String name, String city) {
        return stationRepository.findByName(name).orElseGet(() -> {
            logger.info("Stasiun '{}' tidak ditemukan, membuat baru...", name);
            Station station = new Station();
            station.setName(name);
            station.setCity(city);
            return stationRepository.save(station);
        });
    }

    // Helper method untuk inisialisasi jadwal dengan logika UPDATE jika sudah ada
    private void initializeSchedule(Station departureStation, Station arrivalStation, String trainName,
                                    String departureTimeStr, String arrivalTimeStr,
                                    Double price, Integer availableSeats) {
        
        if (departureStation == null || arrivalStation == null) {
            logger.warn("Tidak dapat memproses jadwal untuk '{}' karena stasiun keberangkatan atau kedatangan null. Lewati.", trainName);
            return;
        }

        LocalDateTime departureTime = LocalDateTime.parse(departureTimeStr);
        LocalDateTime arrivalTime = LocalDateTime.parse(arrivalTimeStr);

        Optional<Schedule> existingScheduleOpt = scheduleRepository
            .findByDepartureStationInfo_StationIdAndArrivalStationInfo_StationIdAndTrainNameAndDepartureTime(
                departureStation.getId(),
                arrivalStation.getId(),
                trainName,
                departureTime
            );

        if (existingScheduleOpt.isPresent()) {
            // JADWAL SUDAH ADA, CEK APAKAH PERLU DIUPDATE
            Schedule scheduleToUpdate = existingScheduleOpt.get();
            boolean needsUpdate = false;

            // Bandingkan dan update field jika berbeda
            // Memastikan EmbeddedStationInfo selalu sinkron dengan data Station master
            if (!scheduleToUpdate.getDepartureStationInfo().getStationId().equals(departureStation.getId()) ||
                !scheduleToUpdate.getDepartureStationInfo().getName().equals(departureStation.getName()) ||
                !scheduleToUpdate.getDepartureStationInfo().getCity().equals(departureStation.getCity())) {
                scheduleToUpdate.setDepartureStationInfo(new EmbeddedStationInfo(departureStation.getId(), departureStation.getName(), departureStation.getCity()));
                needsUpdate = true;
            }

            if (!scheduleToUpdate.getArrivalStationInfo().getStationId().equals(arrivalStation.getId()) ||
                !scheduleToUpdate.getArrivalStationInfo().getName().equals(arrivalStation.getName()) ||
                !scheduleToUpdate.getArrivalStationInfo().getCity().equals(arrivalStation.getCity())) {
                scheduleToUpdate.setArrivalStationInfo(new EmbeddedStationInfo(arrivalStation.getId(), arrivalStation.getName(), arrivalStation.getCity()));
                needsUpdate = true;
            }

            if (!scheduleToUpdate.getArrivalTime().equals(arrivalTime)) {
                scheduleToUpdate.setArrivalTime(arrivalTime);
                needsUpdate = true;
            }

            if (!scheduleToUpdate.getPrice().equals(price)) {
                scheduleToUpdate.setPrice(price);
                needsUpdate = true;
            }

            // Logika untuk availableSeats:
            // Jika Anda ingin DataInitializer selalu me-reset availableSeats ke nilai yang didefinisikan di sini,
            // maka logika di bawah ini benar.
            // Namun, jika Anda ingin availableSeats hanya diinisialisasi saat pembuatan pertama dan
            // kemudian dikelola oleh sistem pemesanan (berkurang saat tiket dipesan),
            // maka Anda sebaiknya MENGHAPUS blok 'if' untuk availableSeats ini dari bagian UPDATE.
            // Untuk tujuan demonstrasi reset/inisialisasi, kita biarkan.
            if (!scheduleToUpdate.getAvailableSeats().equals(availableSeats)) {
                scheduleToUpdate.setAvailableSeats(availableSeats);
                needsUpdate = true;
            }
            
            if (needsUpdate) {
                scheduleRepository.save(scheduleToUpdate);
                logger.info("Jadwal '{}' ({} - {}) DIPERBARUI.", trainName, departureStation.getName(), arrivalStation.getName());
            } else {
                logger.info("Jadwal '{}' ({} - {}) sudah ada dan tidak ada perubahan.", trainName, departureStation.getName(), arrivalStation.getName());
            }

        } else {
            // JADWAL BELUM ADA, BUAT BARU
            Schedule newSchedule = new Schedule();
            newSchedule.setDepartureStationInfo(new EmbeddedStationInfo(departureStation.getId(), departureStation.getName(), departureStation.getCity()));
            newSchedule.setArrivalStationInfo(new EmbeddedStationInfo(arrivalStation.getId(), arrivalStation.getName(), arrivalStation.getCity()));
            newSchedule.setDepartureTime(departureTime);
            newSchedule.setArrivalTime(arrivalTime);
            newSchedule.setTrainName(trainName);
            newSchedule.setPrice(price);
            newSchedule.setAvailableSeats(availableSeats);
            scheduleRepository.save(newSchedule);
            logger.info("Jadwal '{}' ({} - {}) DIBUAT.", trainName, departureStation.getName(), arrivalStation.getName());
        }
    }
}