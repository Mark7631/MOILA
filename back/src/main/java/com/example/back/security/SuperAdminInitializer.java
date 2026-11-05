package com.example.back.security;

import com.example.back.entities.User;
import com.example.back.enums.UserRole;
import com.example.back.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SuperAdminInitializer implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(SuperAdminInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.super-admin.email}")
    private String email;

    @Value("${app.super-admin.password}")
    private String password;

    public SuperAdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            log.info("SPA initialization skipped, some values empty");
            return;
        }

        if (userRepository.getByEmail(email).isPresent()) {
            log.info("SPA initialization skipped, email is busy " + email);
            return;
        }

        User admin = new User();
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRole(UserRole.SUPER_PUPER_ADMIN);
        admin.setActive(true);
        userRepository.save(admin);

        log.warn("Created initial SPA '{}'. REMOVE the APP_SUPER_ADMIN_* env vars", email);
    }
}