package com.example.User.service.runner;

import com.example.User.entities.User;
import com.example.User.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class SeedRunner implements CommandLineRunner {
    @Autowired
    private final UserRepository repo;

    @Autowired
    private final PasswordEncoder encoder;

    /**
     * Seeds the database with an initial admin user if not already present.
     * This method runs on application startup.
     *
     * @param args command line arguments
     * @throws Exception if any error occurs during execution
     */
    @Override
    public void run(String... args) throws Exception {
        repo.findByEmail("admin@example.com").orElseGet(() ->
                repo.save(User.builder()
                        .name("admin")
                        .email("admin@example.com")
                        .password(encoder.encode("admin123"))
                        .roles(Set.of("ROLE_ADMIN"))
                        .build()));
    }
}
