package com.example.spring_security.controllers;

import com.example.spring_security.config.JwtUtil;
import com.example.spring_security.dto.AuthDto;
import com.example.spring_security.dto.UserDto;
import com.example.spring_security.model.User;
import com.example.spring_security.services.UserService;
import com.example.spring_security.util.UserValidator;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.util.Map;


@RestController
public class RegisterController {

    private final UserService userService;
    private final JwtUtil jwt;
    private final ModelMapper modelMapper;
    private final UserValidator userValidator;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public RegisterController(UserService userService, JwtUtil jwt, ModelMapper modelMapper, UserValidator userValidator, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwt = jwt;
        this.modelMapper = modelMapper;
        this.userValidator = userValidator;
        this.authenticationManager = authenticationManager;
    }


    @PostMapping("/login")
    public Map<String, String> performLogin(@RequestBody AuthDto authenticationDTO) {
        // Создаем объект для аутентификации с именем пользователя и паролем
        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(authenticationDTO.getUsername(), authenticationDTO.getPassword());

        try {
            // Пытаемся аутентифицировать пользователя
            Authentication authentication = authenticationManager.authenticate(authInputToken);

            // Если аутентификация успешна, генерируем JWT токен
            String token = jwt.generateToken(authentication.getName());

            // Возвращаем токен в виде JSON-ответа
            return Map.of("jwt-token", token);
        } catch (BadCredentialsException e) {
            // Если аутентификация не прошла, возвращаем сообщение о неверных данных
            return Map.of("message", "Incorrect credentials!");
        }
    }

    // по-хорошему выбрасивать исключение
    @PostMapping("/register")
    public Map<String, String> registerUser(@RequestBody @Valid UserDto userDto,
                               BindingResult bindingResult) {

        User user = convertToUser(userDto);
        userValidator.validate(user, bindingResult);
         if(bindingResult.hasErrors()) {
             return Map.of("massage", "Error");
         }

        userService.registerUser(user.getUsername(), user.getPassword());
        String token = jwt.generateToken(user.getUsername());
        // Возвращаем токен в JSON-ответе
//        return ResponseEntity.ok(Map.of("jwt-token", token));
         return Map.of("jwt-token", token);

    }

    public User convertToUser(UserDto userDto) {
        return this.modelMapper.map(userDto, User.class);
    }
}

