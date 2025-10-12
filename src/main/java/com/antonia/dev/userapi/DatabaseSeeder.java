package com.antonia.dev.userapi;

import com.antonia.dev.userapi.entity.Role;
import com.antonia.dev.userapi.entity.User;
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
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                userRepository.saveAll(List.of(
                        new User("Antonia", "antonia", "antonia@mail.com", passwordEncoder.encode("password1"), Role.USER),
                        new User("Irene", "irene", "irene@mail.com", passwordEncoder.encode("password2"), Role.USER),
                        new User("Lupe", "lupe", "lupe@mail.com", passwordEncoder.encode("password3"), Role.USER),
                        new User("Miguel", "miguel", "miguel@mail.com", passwordEncoder.encode("password4"), Role.USER),
                        new User("Elena", "elena", "elena@mail.com", passwordEncoder.encode("password5"), Role.USER),
                        new User("Rosa", "rosa", "rosa@mail.com", passwordEncoder.encode("password6"), Role.ADMIN),
                        new User("Virginia", "virginia", "virginia@mail.com", passwordEncoder.encode("password7"), Role.USER),
                        new User("Sergio", "sergio", "sergio@mail.com", passwordEncoder.encode("password8"), Role.USER),
                        new User("Héctor", "hector", "hector@mail.com", passwordEncoder.encode("password9"), Role.ADMIN),
                        new User("Mario", "mario", "mario@mail.com", passwordEncoder.encode("password10"), Role.USER)
                ));
                logger.info("Database seeded with sample users.");
            }
        };
    }
}

