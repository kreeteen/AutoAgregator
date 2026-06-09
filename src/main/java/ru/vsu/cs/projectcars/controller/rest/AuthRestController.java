package ru.vsu.cs.projectcars.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.projectcars.dto.LoginRequest;
import ru.vsu.cs.projectcars.dto.RegisterRequest;
import ru.vsu.cs.projectcars.dto.UserSession;
import ru.vsu.cs.projectcars.service.UserService;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Аутентификация")
@Transactional
public class AuthRestController {

    private final UserService userService;

    public AuthRestController(UserService userService) {
        this.userService = userService;
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
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }
}
