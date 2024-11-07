package ru.tbank.springapp.service.jwt;

import org.springframework.security.core.userdetails.UserDetails;
import ru.tbank.springapp.model.entities.auth.UserEntity;
import ru.tbank.springapp.model.entities.auth.jwt.JwtEntity;

import java.time.LocalDateTime;

public interface JwtService {
    String extractUsername(String token);

    void revokeTokenByUsername(String username);

    LocalDateTime extractExpirationDate(String token);

    JwtEntity generateToken(UserEntity user, boolean rememberMe);

    boolean isTokenValid(String token, UserDetails userDetails);
}
