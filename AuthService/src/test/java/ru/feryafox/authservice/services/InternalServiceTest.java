package ru.feryafox.authservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.feryafox.authservice.entities.User;
import ru.feryafox.authservice.exceptions.user.UserIsNotExistException;
import ru.feryafox.authservice.repositories.UserRepository;
import ru.feryafox.models.internal.responses.UserProfileResponse;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InternalServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private InternalService internalService;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setPhoneNumber("+79991234567");
        user.setFirstName("Иван");
        user.setSurname("Петров");
        user.setMiddleName("Сергеевич");
    }

    @Test
    void getBuyerProfileServiceByUser_UserExists_ShouldReturnProfile() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserProfileResponse response = internalService.getBuyerProfileServiceByUser(userId.toString());

        assertNotNull(response);
        assertEquals(userId.toString(), response.getId());
        assertEquals(user.getPhoneNumber(), response.getPhone());
        assertEquals(user.getFirstName(), response.getFirstName());
        assertEquals(user.getSurname(), response.getSurname());
        assertEquals(user.getMiddleName(), response.getMiddleName());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getBuyerProfileServiceByUser_UserNotExists_ShouldThrowException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserIsNotExistException.class, () -> internalService.getBuyerProfileServiceByUser(userId.toString()));

        verify(userRepository, times(1)).findById(userId);
    }
}
