package ru.tbank.springapp.service.auth;

import ru.tbank.springapp.dto.auth.UserLoginRequestDTO;
import ru.tbank.springapp.dto.auth.UserRegisterRequestDTO;
import ru.tbank.springapp.dto.auth.jwt.AuthenticationTokenResponse;

public interface AuthenticationService {
    AuthenticationTokenResponse register(UserRegisterRequestDTO userRegisterRequestDTO);

    AuthenticationTokenResponse login(UserLoginRequestDTO userLoginRequestDTO);

    void logout(String username);
}
