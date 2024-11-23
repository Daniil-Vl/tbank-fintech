package ru.tbank.springapp.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.springapp.model.entities.auth.UserEntity;

@RestController
@RequestMapping("/api/v1/demo")
@Slf4j
public class AuthDemoController {

    @GetMapping
    public String demo() {
        log.info("Someone trying to get access to demo get method");
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return String.format("Hello user %s from secured endpoint", user.getUsername());
    }

    @PostMapping("")
    public String post() {
        log.info("Someone trying to get access to demo post method");
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return String.format("Hello user %s from secured post endpoint", user.getUsername());
    }

}
