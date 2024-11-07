package ru.tbank.springapp.service.user;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.tbank.springapp.dto.auth.UserRegisterRequestDTO;
import ru.tbank.springapp.model.entities.auth.UserEntity;

public interface UserService extends UserDetailsService {
    UserEntity createUser(UserRegisterRequestDTO userRegisterRequestDTO);

    boolean isExists(String username);

    void changePassword(String username, String oldPassword, String newPassword);
}
