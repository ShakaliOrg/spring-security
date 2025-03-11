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

        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(authenticationDTO.getUsername(), authenticationDTO.getPassword());

        try {
            Authentication authentication = authenticationManager.authenticate(authInputToken);
            String token = jwt.generateToken(authentication.getName());
            return Map.of("jwt-token", "Login success!");
        } catch (BadCredentialsException e) {
            return Map.of("message", "Incorrect credentials!");
        }
    }

    // по-хорошему выбрасивать исключение
    @PostMapping("/register")
    public Map<String, String> performRegister(@RequestBody @Valid UserDto userDto,
                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));
            return Map.of("message", "Error");
        }

        userService.registerUser(userDto.getUsername(), userDto.getPassword());
        String token = jwt.generateToken(userDto.getUsername());
        return Map.of("jwt-token", token);
    }

    public User convertToUser(UserDto userDto) {
        return this.modelMapper.map(userDto, User.class);
    }
}

