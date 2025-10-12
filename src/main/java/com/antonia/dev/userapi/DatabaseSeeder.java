package com.antonia.dev.userapi;

import com.antonia.dev.userapi.entity.Role;
import com.antonia.dev.userapi.entity.User;
import com.antonia.dev.userapi.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.CommandLineRunner;

import java.util.List;

@Configuration
@Profile("dev")
public class DatabaseSeeder {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                userRepository.saveAll(List.of(
                        new User(null, "Antonia", "antonia", "antonia@mail.com", passwordEncoder.encode("password1"), Role.USER),
                        new User(null, "Irene", "irene", "irene@mail.com", passwordEncoder.encode("password2"), Role.USER),
                        new User(null, "Lupe", "lupe", "lupe@mail.com", passwordEncoder.encode("password3"), Role.USER),
                        new User(null, "Miguel", "miguel", "miguel@mail.com", passwordEncoder.encode("password4"), Role.USER),
                        new User(null, "Elena", "elena", "elena@mail.com", passwordEncoder.encode("password5"), Role.USER),
                        new User(null, "Rosa", "rosa", "rosa@mail.com", passwordEncoder.encode("password6"), Role.ADMIN),
                        new User(null, "Virginia", "virginia", "virginia@mail.com", passwordEncoder.encode("password7"), Role.USER),
                        new User(null, "Sergio", "sergio", "sergio@mail.com", passwordEncoder.encode("password8"), Role.USER),
                        new User(null, "Héctor", "hector", "hector@mail.com", passwordEncoder.encode("password9"), Role.ADMIN),
                        new User(null, "Mario", "mario", "mario@mail.com", passwordEncoder.encode("password10"), Role.USER)
                ));
                System.out.println("✅ Database seeded with sample users.");
            }
        };
    }
}

