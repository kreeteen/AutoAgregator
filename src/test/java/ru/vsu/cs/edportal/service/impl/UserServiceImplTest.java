package ru.vsu.cs.edportal.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.vsu.cs.edportal.dto.LoginRequest;
import ru.vsu.cs.edportal.dto.RegisterRequest;
import ru.vsu.cs.edportal.dto.UserSession;
import ru.vsu.cs.edportal.model.User;
import ru.vsu.cs.edportal.repository.UserRepository;
import ru.vsu.cs.edportal.security.JwtTokenProvider;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @InjectMocks private UserServiceImpl userService;

    @Test
    @DisplayName("Register should create user and return session")
    void register() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("test@test.ru");
        req.setPassword("pass");
        req.setFirstName("Иван");
        req.setLastName("Петров");
        req.setPhoneNumber("+7 999 111-22-33");

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hash123");
        when(jwtTokenProvider.createToken(any(), any(), any())).thenReturn("jwt-token");
        when(userRepository.save(any())).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(1);
            return u;
        });

        UserSession session = userService.register(req);
        assertNotNull(session);
        assertEquals("test@test.ru", session.getEmail());
        assertEquals("Иван", session.getFirstName());
        assertEquals("jwt-token", session.getToken());
    }

    @Test
    @DisplayName("Register duplicate email should throw")
    void registerDuplicate() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("dup@test.ru");
        when(userRepository.existsByEmail(any())).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> userService.register(req));
    }

    @Test
    @DisplayName("Authenticate should return session on success")
    void authenticateSuccess() {
        User user = new User();
        user.setId(1);
        user.setEmail("a@b.ru");
        user.setFirstName("A");
        user.setPasswordHash("hash");

        when(userRepository.findByEmail("a@b.ru")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "hash")).thenReturn(true);
        when(jwtTokenProvider.createToken(any(), any(), any())).thenReturn("jwt-token");

        LoginRequest req = new LoginRequest();
        req.setEmail("a@b.ru");
        req.setPassword("pass");

        Optional<UserSession> opt = userService.authenticate(req);
        assertTrue(opt.isPresent());
        assertEquals("A", opt.get().getFirstName());
        assertEquals("jwt-token", opt.get().getToken());
    }

    @Test
    @DisplayName("Authenticate wrong password should return empty")
    void authenticateWrong() {
        User user = new User();
        user.setEmail("a@b.ru");
        user.setPasswordHash("hash");

        when(userRepository.findByEmail("a@b.ru")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hash")).thenReturn(false);

        LoginRequest req = new LoginRequest();
        req.setEmail("a@b.ru");
        req.setPassword("wrong");

        assertTrue(userService.authenticate(req).isEmpty());
    }
}
