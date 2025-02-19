package ru.feryafox.authservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.feryafox.authservice.repositories.UserRepository;
import ru.feryafox.utils.UUIDUtils;

import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        if (UUIDUtils.isUUID(identifier)) {
            return userRepository.findById(UUID.fromString(identifier))
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + identifier));
        } else {
            return userRepository.findByPhoneNumber(identifier)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with phone number: " + identifier));
        }
    }
}
