package ru.tbank.springapp.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.tbank.springapp.dao.jpa.auth.UserRepository;
import ru.tbank.springapp.dto.auth.UserRegisterRequestDTO;
import ru.tbank.springapp.exception.auth.WrongPasswordException;
import ru.tbank.springapp.model.entities.auth.UserEntity;
import ru.tbank.springapp.model.entities.auth.role.Role;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("No user found with username '%s'", username)
                ));
    }

    @Override
    public UserEntity createUser(UserRegisterRequestDTO requestBody) {
        UserEntity user = UserEntity.builder()
                .username(requestBody.username())
                .password(
                        passwordEncoder.encode(requestBody.password())
                )
                .role(Role.USER)
                .build();
        return userRepository.save(user);
    }

    @Override
    public boolean isExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("User with username %s not found", username)
                ));

        String encoded = passwordEncoder.encode(oldPassword);
        if (!encoded.equals(userEntity.getPassword())) {
            log.warn("Wrong password for user: {}", username);
            throw new WrongPasswordException("Wrong password");
        }

        userEntity.setPassword(passwordEncoder.encode(newPassword));
        log.info("Password successfully change for user: {}", username);
    }
}
