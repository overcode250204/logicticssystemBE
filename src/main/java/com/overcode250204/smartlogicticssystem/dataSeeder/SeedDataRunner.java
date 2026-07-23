//package com.overcode250204.smartlogicticssystem.dataSeeder;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.Comparator;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class SeedDataRunner implements CommandLineRunner {
//
//    private final List<DataSeeder> dataSeeders;
//    private final DatabaseSequenceSynchronizer databaseSequenceSynchronizer;
//
//    @Override
//    public void run(String... args) {
//        log.info("========== START SEED DATA ==========");
//        databaseSequenceSynchronizer.synchronize();
//
//        dataSeeders.stream()
//                .sorted(Comparator.comparingInt(DataSeeder::getOrder))
//                .forEach(seeder -> {
//                    String seederName = seeder.getClass().getSimpleName();
//
//                    try {
//                        log.info("Running seeder: {}", seederName);
//                        seeder.seed();
//                        log.info("Completed seeder: {}", seederName);
//                    } catch (Exception exception) {
//                        log.error("Seeder failed: {}", seederName, exception);
//                        throw new IllegalStateException(
//                                "Cannot run seed data: " + seederName,
//                                exception
//                        );
//                    }
//                });
//
//        log.info("========== FINISH SEED DATA ==========");
//    }
//}
