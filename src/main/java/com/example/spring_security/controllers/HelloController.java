package com.example.spring_security.controllers;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public Map<String, String> getHelloMessage() {
        // Получаем имя пользователя из SecurityContext
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Возвращаем имя пользователя в виде JSON
        return Map.of("message", "Hello, " + username + "!");
    }
}

