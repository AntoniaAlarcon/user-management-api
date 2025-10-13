package com.antonia.dev.userapi;

import com.antonia.dev.userapi.entity.Role;
import com.antonia.dev.userapi.entity.User;
import com.antonia.dev.userapi.repository.RoleRepository;
import com.antonia.dev.userapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.CommandLineRunner;

import java.util.List;

@Configuration
@Profile("dev")
public class DatabaseSeeder {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,
                                   RoleRepository roleRepository,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            if (roleRepository.count() == 0) {
                roleRepository.saveAll(List.of(
                        new Role("ADMIN", "Administrator - full system access"),
                        new Role("USER", "Regular user - basic access"),
                        new Role("MANAGER", "Manager - limited administrative access")
                ));
                logger.info("Database seeded with default roles.");
            }

            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
            Role userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("USER role not found"));
            Role managerRole = roleRepository.findByName("MANAGER")
                    .orElseThrow(() -> new RuntimeException("MANAGER role not found"));

            if (userRepository.count() == 0) {
                userRepository.saveAll(List.of(
                        new User("Antonia", "antonia", "antonia@mail.com", passwordEncoder.encode("password1"), userRole),
                        new User("Irene", "irene", "irene@mail.com", passwordEncoder.encode("password2"), userRole),
                        new User("Lupe", "lupe", "lupe@mail.com", passwordEncoder.encode("password3"), userRole),
                        new User("Miguel", "miguel", "miguel@mail.com", passwordEncoder.encode("password4"), userRole),
                        new User("Elena", "elena", "elena@mail.com", passwordEncoder.encode("password5"), userRole),
                        new User("Rosa", "rosa", "rosa@mail.com", passwordEncoder.encode("password6"), adminRole),
                        new User("Virginia", "virginia", "virginia@mail.com", passwordEncoder.encode("password7"), userRole),
                        new User("Sergio", "sergio", "sergio@mail.com", passwordEncoder.encode("password8"), userRole),
                        new User("Héctor", "hector", "hector@mail.com", passwordEncoder.encode("password9"), adminRole),
                        new User("Mario", "mario", "mario@mail.com", passwordEncoder.encode("password10"), managerRole)
                ));
                logger.info("Database seeded with sample users.");
            }

        };
    }

}

