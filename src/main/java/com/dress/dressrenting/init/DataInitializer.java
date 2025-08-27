package com.dress.dressrenting.init;

import com.dress.dressrenting.model.User;
import com.dress.dressrenting.model.enums.UserRole;
import com.dress.dressrenting.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            String defaultEmail = "admin@example.com";
            String defaultPassword = "admin123";

            if (userRepository.findByEmail(defaultEmail).isEmpty()) {
                User admin = User.builder()
                        .name("Admin")
                        .surname("User")
                        .email(defaultEmail)
                        .password(new BCryptPasswordEncoder().encode(defaultPassword))
                        .phone("0000000000")
                        .userRole(UserRole.ADMIN)
                        .active(true)
                        .isAccountNonExpired(true)
                        .isAccountNonLocked(true)
                        .isCredentialsNonExpired(true)
                        .isEnabled(true)
                        .build();

                userRepository.save(admin);
                System.out.println("Admin was created: " + defaultEmail + "/" + defaultPassword);
            }
        };
    }
}
