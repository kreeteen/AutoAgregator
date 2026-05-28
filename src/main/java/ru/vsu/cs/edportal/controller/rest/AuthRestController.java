package ru.vsu.cs.edportal.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.edportal.dto.LoginRequest;
import ru.vsu.cs.edportal.dto.RegisterRequest;
import ru.vsu.cs.edportal.dto.UserSession;
import ru.vsu.cs.edportal.security.JwtAuthFilter;
import ru.vsu.cs.edportal.security.JwtTokenBlacklist;
import ru.vsu.cs.edportal.service.UserService;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Аутентификация")
@Transactional
public class AuthRestController {

    private final UserService userService;
    private final JwtTokenBlacklist tokenBlacklist;

    public AuthRestController(UserService userService, JwtTokenBlacklist tokenBlacklist) {
        this.userService = userService;
        this.tokenBlacklist = tokenBlacklist;
    }

    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            UserSession session = userService.register(request);
            return ResponseEntity.ok(session);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Вход в систему")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Optional<UserSession> user = userService.authenticate(request);
        if (user.isEmpty()) {
            return ResponseEntity.status(401).body("Неверный email или пароль");
        }
        return ResponseEntity.ok(user.get());
    }

    @PostMapping("/logout")
    @Operation(summary = "Выход из системы")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String token = JwtAuthFilter.extractTokenFromRequest(request);
        if (token != null) tokenBlacklist.revoke(token);
        jakarta.servlet.http.HttpServletResponse resp = (jakarta.servlet.http.HttpServletResponse)
                request.getAttribute("jakarta.servlet.http.HttpServletResponse");
        if (resp != null) JwtAuthFilter.clearJwtCookie(resp);
        return ResponseEntity.ok().build();
    }
}
