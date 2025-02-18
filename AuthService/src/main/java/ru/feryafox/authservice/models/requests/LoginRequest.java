package ru.feryafox.authservice.models.requests;

import lombok.Data;

@Data
public class LoginRequest {
    private String phone;
    private String password;
}
