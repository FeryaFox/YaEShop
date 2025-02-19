package ru.feryafox.authservice.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.feryafox.authservice.entities.RefreshToken;
import ru.feryafox.authservice.entities.Role;
import ru.feryafox.authservice.entities.User;
import ru.feryafox.authservice.exceptions.token.RefreshTokenIsNotExistException;
import ru.feryafox.authservice.exceptions.user.UserIsExistException;
import ru.feryafox.authservice.models.requests.LoginRequest;
import ru.feryafox.authservice.models.requests.RegisterRequest;
import ru.feryafox.authservice.models.responses.AuthResponse;
import ru.feryafox.authservice.repositories.RefreshTokenRepository;
import ru.feryafox.authservice.repositories.RoleRepository;
import ru.feryafox.authservice.repositories.UserRepository;
import ru.feryafox.authservice.utils.IpAddressUtils;
import ru.feryafox.jwt.JwtUtils;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;

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

    @Transactional
    public AuthResponse login(LoginRequest loginRequest, HttpServletRequest request) {

        User user;

        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getPhoneNumber(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            user = (User) authentication.getPrincipal();
        }
        catch (AuthenticationException e) {
            e.printStackTrace();
            throw e;
        }
        ru.feryafox.jwt.dto.RefreshToken refreshTokenObj = jwtUtils.generateRefreshToken(user.getId());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenObj.getRefreshToken());
        refreshToken.setUser(user);
        refreshToken.setIpAddress(IpAddressUtils.getClientIp(request));
        refreshToken.setExpiredAt(refreshTokenObj.getExpiredAt());

        refreshTokenRepository.save(refreshToken);

        String accessToken = jwtUtils.generateToken(user.getId());

        return new AuthResponse(accessToken, refreshTokenObj.getRefreshToken());
    }

    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        jwtUtils.validateToken(refreshToken);

        RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken).orElseThrow(() -> new RefreshTokenIsNotExistException(String.format("Рефреш токен %s нет в бд", refreshToken)));

        User tokenOwner = refreshTokenEntity.getUser();

        ru.feryafox.jwt.dto.RefreshToken newRefreshToken = jwtUtils.generateRefreshToken(tokenOwner.getId());

        refreshTokenEntity.setToken(newRefreshToken.getRefreshToken());
        refreshTokenEntity.setExpiredAt(newRefreshToken.getExpiredAt());

        refreshTokenRepository.save(refreshTokenEntity);

        String accessToken = jwtUtils.generateToken(tokenOwner.getId());

        return new AuthResponse(accessToken, refreshToken);
    }
}
