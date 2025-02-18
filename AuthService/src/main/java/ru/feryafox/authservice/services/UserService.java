package ru.feryafox.authservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.feryafox.authservice.entities.Role;
import ru.feryafox.authservice.entities.User;
import ru.feryafox.authservice.exceptions.UserIsExistException;
import ru.feryafox.authservice.models.requests.LoginRequest;
import ru.feryafox.authservice.models.requests.RegisterRequest;
import ru.feryafox.authservice.repositories.RoleRepository;
import ru.feryafox.authservice.repositories.UserRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(RegisterRequest registerRequest) {
        if (userRepository.existsByPhoneNumber(registerRequest.getPhoneNumber())) {
            throw new UserIsExistException(registerRequest.getPhoneNumber());
        }

        User user = new User();
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setFirstName(registerRequest.getFirstName());
        user.setSurname(registerRequest.getSurname());
        user.setMiddleName(registerRequest.getMiddleName());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));

        Role role = roleRepository.findByName(registerRequest.getRole().equals("SELLER") ? Role.RoleName.SELLER : Role.RoleName.BUYER).get();

        user.getRoles().add(role);

        userRepository.save(user);
    }
}
