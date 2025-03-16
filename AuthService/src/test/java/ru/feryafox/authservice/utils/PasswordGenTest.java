package ru.feryafox.authservice.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordGenTest {

    private PasswordGen passwordGen;

    @BeforeEach
    void setUp() {
        passwordGen = new PasswordGen();
    }

    @Test
    void generatePassword_ShouldReturnValidPassword() {
        int length = 12;
        String password = passwordGen.generatePassword(length);

        assertNotNull(password);
        assertEquals(length, password.length());
        assertTrue(password.matches(".*[A-Z].*"), "Пароль должен содержать хотя бы одну заглавную букву");
        assertTrue(password.matches(".*[a-z].*"), "Пароль должен содержать хотя бы одну строчную букву");
        assertTrue(password.matches(".*\\d.*"), "Пароль должен содержать хотя бы одну цифру");
        assertTrue(password.matches(".*[!@#$%^&*()].*"), "Пароль должен содержать хотя бы один специальный символ");
    }

    @Test
    void generatePassword_ZeroLength_ShouldReturnEmptyString() {
        String password = passwordGen.generatePassword(0);
        assertNotNull(password);
        assertEquals(0, password.length());
    }

    @Test
    void generatePassword_NegativeLength_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> passwordGen.generatePassword(-5));
    }

    @Test
    void generatePassword_ShortLength_ShouldStillBeValid() {
        int length = 4; // минимальная длина
        String password = passwordGen.generatePassword(length);

        assertNotNull(password);
        assertEquals(length, password.length());
    }
}
