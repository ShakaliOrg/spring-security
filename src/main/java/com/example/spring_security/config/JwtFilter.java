package com.example.spring_security.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtFilter(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        // Проверяем, начинается ли заголовок с "Bearer "
        if (authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);  // Извлекаем токен (после "Bearer ")

            if (jwt.isBlank()) {
                // Если токен пустой, отправляем ошибку
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT Token in Bearer Header");
                return;
            }

            try {
                // Валидируем токен и извлекаем username
                String username = jwtUtil.validateTokenAndRetrieveClaim(jwt);

                // Загружаем пользователя через CustomUserDetailsService
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                // Создаем объект аутентификации для Spring Security
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,  // Пароль не требуется для аутентификации через JWT
                        userDetails.getAuthorities()  // Даем роли пользователя
                );

                // Устанавливаем аутентификацию в контекст безопасности, если она еще не установлена
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // В случае ошибки валидации токена
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or Expired JWT Token");
                return;
            }
        }

        // Переходим к следующему фильтру в цепочке
        filterChain.doFilter(request, response);
    }
}