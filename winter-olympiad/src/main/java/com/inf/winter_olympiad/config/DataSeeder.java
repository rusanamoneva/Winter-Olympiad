package com.inf.winter_olympiad.config;

import com.inf.winter_olympiad.entity.User;
import com.inf.winter_olympiad.entity.enums.Role;
import com.inf.winter_olympiad.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedAdminUser() {
        return args -> {
            if (userRepository.existsByUsername("admin")) {
                return;
            }

            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@winter.local");
            admin.setPassword(passwordEncoder.encode("Admin123!"));
            admin.setRole(Role.ADMIN);
            admin.setEnabled(true);
            userRepository.save(admin);
        };
    }
}

