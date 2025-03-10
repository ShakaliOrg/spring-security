package com.example.spring_security.services;


import com.example.spring_security.model.User;
import com.example.spring_security.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerUser(String username, String password) {
        System.out.println("Начало регистрации пользователя: " + username); // Проверка

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user); // <-- Сохранение в БД
        System.out.println("Пользователь сохранен в БД!"); // Проверка
    }


    public final Optional<User> getUserByUsername(String name) {
        return userRepository.findByUsername(name);
    }
}
