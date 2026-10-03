package com.gsdeveloper.bookmyslot.config;

import com.gsdeveloper.bookmyslot.entity.User;
import com.gsdeveloper.bookmyslot.enums.Role;
import com.gsdeveloper.bookmyslot.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DefaultAdminInitializer {

    private static final String ADMIN_EMAIL = "Admin123@gmail.com";
    private static final String ADMIN_PASSWORD = "Admin@123";

    @Bean
    CommandLineRunner createDefaultAdmin(UserRepository users, PasswordEncoder passwordEncoder) {
        return args -> {
            if (users.existsByEmail(ADMIN_EMAIL)) return;

            User admin = new User();
            admin.setFullName("BookMySlot Administrator");
            admin.setEmail(ADMIN_EMAIL);
            admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
            admin.setRole(Role.ADMIN);
            users.save(admin);
        };
    }
}
