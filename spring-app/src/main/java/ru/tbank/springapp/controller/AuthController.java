package ru.tbank.springapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.springapp.dto.auth.UserLoginRequestDTO;
import ru.tbank.springapp.dto.auth.UserLogoutDTO;
import ru.tbank.springapp.dto.auth.UserRegisterRequestDTO;
import ru.tbank.springapp.dto.auth.jwt.AuthenticationTokenResponse;
import ru.tbank.springapp.service.auth.AuthenticationService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public AuthenticationTokenResponse register(
            @RequestBody @Valid UserRegisterRequestDTO userRegisterRequestDTO
    ) {
        log.info("Registering user {}", userRegisterRequestDTO);
        return authenticationService.register(userRegisterRequestDTO);
    }

    @PostMapping("/login")
    public AuthenticationTokenResponse login(
            @RequestBody @Valid UserLoginRequestDTO userLoginRequestDTO
    ) {
        log.info("Login user {}", userLoginRequestDTO);
        return authenticationService.login(userLoginRequestDTO);
    }

    @PostMapping("/logout")
    public void logout(
            @RequestBody @Valid UserLogoutDTO userLogoutDTO
    ) {
        authenticationService.logout(userLogoutDTO.username());
    }

}
