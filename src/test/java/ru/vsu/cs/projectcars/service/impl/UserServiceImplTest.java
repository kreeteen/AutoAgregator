package ru.vsu.cs.projectcars.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.vsu.cs.projectcars.dto.LoginRequest;
import ru.vsu.cs.projectcars.dto.RegisterRequest;
import ru.vsu.cs.projectcars.dto.UserSession;
import ru.vsu.cs.projectcars.model.Role;
import ru.vsu.cs.projectcars.model.User;
import ru.vsu.cs.projectcars.repository.RoleRepository;
import ru.vsu.cs.projectcars.repository.UserRepository;
import ru.vsu.cs.projectcars.security.JwtTokenProvider;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
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

        Role userRole = new Role("USER");
        userRole.setId(1);

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hash123");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(jwtTokenProvider.createToken(any(), any(), any(), anyList())).thenReturn("jwt-token");
        when(userRepository.save(any())).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(1);
            u.setRoles(Set.of(userRole));
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
        user.setActive(true);

        when(userRepository.findByEmail("a@b.ru")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "hash")).thenReturn(true);
        when(jwtTokenProvider.createToken(any(), any(), any(), anyList())).thenReturn("jwt-token");

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
        user.setActive(true);

        when(userRepository.findByEmail("a@b.ru")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hash")).thenReturn(false);

        LoginRequest req = new LoginRequest();
        req.setEmail("a@b.ru");
        req.setPassword("wrong");

        assertTrue(userService.authenticate(req).isEmpty());
    }
}
