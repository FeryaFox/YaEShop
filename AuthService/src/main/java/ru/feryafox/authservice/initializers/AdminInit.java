package ru.feryafox.authservice.initializers;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.feryafox.authservice.entities.Role;
import ru.feryafox.authservice.entities.User;
import ru.feryafox.authservice.repositories.RoleRepository;
import ru.feryafox.authservice.repositories.UserRepository;
import ru.feryafox.authservice.services.AuthService;
import ru.feryafox.authservice.utils.PasswordGen;

import java.util.HashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class AdminInit {

    @Bean
    public CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        return args -> {
            String adminPhone = "+71112223344";

            if (!userRepository.findByPhoneNumber(adminPhone).isPresent()) {
                String password = PasswordGen.generatePassword(16);

                Role role = roleRepository.findByName(Role.RoleName.ROLE_ADMIN).get();

                Set<Role> roles = new HashSet<>();
                roles.add(role);

                User user = User.builder()
                        .phoneNumber(adminPhone)
                        .passwordHash(passwordEncoder.encode(password))
                        .firstName("Админ")
                        .surname("Лис")
                        .middleName("middleName")
                        .roles(roles)
                        .build();

                userRepository.save(user);

                System.out.printf("Пароль: %s\n", password);
            }
        };
    }
}
