package ru.vsu.cs.edportal.service.impl;

import org.springframework.stereotype.Service;
import ru.vsu.cs.edportal.dto.LoginRequest;
import ru.vsu.cs.edportal.dto.RegisterRequest;
import ru.vsu.cs.edportal.dto.UserSession;
import ru.vsu.cs.edportal.dto.UserUpdateRequest;
import ru.vsu.cs.edportal.model.User;
import ru.vsu.cs.edportal.repository.UserRepository;
import ru.vsu.cs.edportal.security.JwtTokenProvider;
import ru.vsu.cs.edportal.service.UserService;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
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
        user = userRepository.save(user);
        UserSession session = toSession(user);
        session.setToken(jwtTokenProvider.createToken(user.getId(), user.getEmail(), user.getFirstName()));
        return session;
    }

    @Override
    public Optional<UserSession> authenticate(LoginRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPasswordHash()))
                .map(user -> {
                    UserSession session = toSession(user);
                    session.setToken(jwtTokenProvider.createToken(user.getId(), user.getEmail(), user.getFirstName()));
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

    private UserSession toSession(User user) {
        return new UserSession(user.getId(), user.getEmail(),
                user.getFirstName(), user.getLastName());
    }
}
