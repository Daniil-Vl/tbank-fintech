package ru.tbank.springapp.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.tbank.springapp.configuration.ApplicationConfig;
import ru.tbank.springapp.dao.jpa.auth.JwtRepository;
import ru.tbank.springapp.model.entities.auth.UserEntity;
import ru.tbank.springapp.model.entities.auth.jwt.JwtEntity;

import java.security.Key;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {

    private final ApplicationConfig applicationConfig;
    private final JwtRepository jwtRepository;

    public String extractUsername(String token) {
        return getClaims(token).get("sub", String.class);
    }

    @Override
    @Transactional
    public void revokeTokenByUsername(String username) {
        jwtRepository.revokeByUsername(username);
    }

    public LocalDateTime extractExpirationDate(String token) {
        Claims claims = getClaims(token);
        return claims
                .getExpiration()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public JwtEntity generateToken(
            UserEntity user,
            boolean rememberMe
    ) {
        String token = Jwts
                .builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(rememberMe
                        ? new Date(System.currentTimeMillis() + Duration.ofDays(30).toMillis())
                        : new Date(System.currentTimeMillis() + Duration.ofMinutes(10).toMillis()))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();

        JwtEntity jwtEntity = JwtEntity.builder()
                .token(token)
                .user(user)
                .revoked(false)
                .build();

        return jwtRepository.save(jwtEntity);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        JwtEntity jwtEntity = jwtRepository.findByToken(token);
        return username != null
                && username.equals(userDetails.getUsername())
                && !isTokenExpired(token)
                && !jwtEntity.isRevoked();
    }

    private boolean isTokenExpired(String token) {
        return extractExpirationDate(token)
                .isBefore(LocalDateTime.now());
    }

    private Claims getClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] decoded = Decoders.BASE64.decode(applicationConfig.jwtPublicKey());
        return Keys.hmacShaKeyFor(decoded);
    }
}
