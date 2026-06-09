package ru.vsu.cs.projectcars.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.vsu.cs.projectcars.dto.LoginRequest;
import ru.vsu.cs.projectcars.dto.RegisterRequest;
import ru.vsu.cs.projectcars.dto.UserSession;
import ru.vsu.cs.projectcars.dto.UserUpdateRequest;
import ru.vsu.cs.projectcars.model.Role;
import ru.vsu.cs.projectcars.model.User;
import ru.vsu.cs.projectcars.repository.RoleRepository;
import ru.vsu.cs.projectcars.repository.UserRepository;
import ru.vsu.cs.projectcars.security.JwtTokenProvider;
import ru.vsu.cs.projectcars.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public UserSession register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email уже зарегистрирован");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Default role USER not found"));
        user.setRoles(Set.of(userRole));

        user = userRepository.save(user);
        UserSession session = toSession(user);
        List<String> roleNames = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        session.setToken(jwtTokenProvider.createToken(user.getId(), user.getEmail(), user.getFirstName(), roleNames));
        return session;
    }

    @Override
    public Optional<UserSession> authenticate(LoginRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .filter(User::isActive)
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPasswordHash()))
                .map(user -> {
                    UserSession session = toSession(user);
                    List<String> roleNames = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
                    session.setToken(jwtTokenProvider.createToken(user.getId(), user.getEmail(), user.getFirstName(), roleNames));
                    return session;
                });
    }

    @Override
    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public String getPhoneNumber(Integer userId) {
        return userRepository.findById(userId)
                .map(User::getPhoneNumber)
                .orElse(null);
    }

    @Override
    public User updateProfile(Integer userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        if (request.getFirstName() != null && !request.getFirstName().isBlank())
            user.setFirstName(request.getFirstName());
        if (request.getLastName() != null)
            user.setLastName(request.getLastName());
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank())
            user.setPhoneNumber(request.getPhoneNumber());
        return userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findByIdWithRoles(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public User updateUserRoles(Integer userId, List<String> roleNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        Set<Role> roles = roleNames.stream()
                .map(name -> roleRepository.findByName(name)
                        .orElseThrow(() -> new IllegalArgumentException("Роль " + name + " не найдена")))
                .collect(Collectors.toSet());
        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Override
    public User toggleUserActive(Integer userId, boolean active, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        user.setActive(active);
        if (!active) {
            user.setBannedAt(LocalDateTime.now());
            user.setBanReason(reason);
        } else {
            user.setBannedAt(null);
            user.setBanReason(null);
        }
        return userRepository.save(user);
    }

    private UserSession toSession(User user) {
        return new UserSession(user.getId(), user.getEmail(),
                user.getFirstName(), user.getLastName());
    }
}
