package ru.tbank.springapp.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.tbank.springapp.dao.jpa.auth.UserRepository;
import ru.tbank.springapp.dto.auth.UserLoginRequestDTO;
import ru.tbank.springapp.dto.auth.UserRegisterRequestDTO;
import ru.tbank.springapp.dto.auth.jwt.AuthenticationTokenResponse;
import ru.tbank.springapp.model.entities.auth.UserEntity;
import ru.tbank.springapp.model.entities.auth.jwt.JwtEntity;
import ru.tbank.springapp.service.jwt.JwtService;
import ru.tbank.springapp.service.user.UserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @Override
    public AuthenticationTokenResponse register(UserRegisterRequestDTO requestBody) {
        UserEntity user = userService.createUser(requestBody);
        JwtEntity jwt = jwtService.generateToken(user, requestBody.rememberMe());
        return new AuthenticationTokenResponse(jwt.getToken());
    }

    @Override
    public AuthenticationTokenResponse login(UserLoginRequestDTO userLoginRequestDTO) {
        log.info("Start AuthService login method");
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginRequestDTO.username(),
                        userLoginRequestDTO.password()
                )
        );

        UserEntity user = userRepository.findByUsername(userLoginRequestDTO.username())
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("No user found with username '%s'", userLoginRequestDTO.username())
                ));

        JwtEntity jwt = jwtService.generateToken(user, userLoginRequestDTO.rememberMe());
        return new AuthenticationTokenResponse(jwt.getToken());
    }

    @Override
    public void logout(String username) {
        jwtService.revokeTokenByUsername(username);
    }

}
