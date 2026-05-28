package ru.vsu.cs.edportal.service;

import ru.vsu.cs.edportal.dto.LoginRequest;
import ru.vsu.cs.edportal.dto.RegisterRequest;
import ru.vsu.cs.edportal.dto.UserSession;
import ru.vsu.cs.edportal.dto.UserUpdateRequest;
import ru.vsu.cs.edportal.model.User;

import java.util.Optional;

public interface UserService {
    UserSession register(RegisterRequest request);
    Optional<UserSession> authenticate(LoginRequest request);
    Optional<User> findById(Integer id);
    String getPhoneNumber(Integer userId);
    User updateProfile(Integer userId, UserUpdateRequest request);
}
