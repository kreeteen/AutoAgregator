package ru.vsu.cs.projectcars.service;

import ru.vsu.cs.projectcars.dto.LoginRequest;
import ru.vsu.cs.projectcars.dto.RegisterRequest;
import ru.vsu.cs.projectcars.dto.UserSession;
import ru.vsu.cs.projectcars.dto.UserUpdateRequest;
import ru.vsu.cs.projectcars.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserSession register(RegisterRequest request);
    Optional<UserSession> authenticate(LoginRequest request);
    Optional<User> findById(Integer id);
    String getPhoneNumber(Integer userId);
    User updateProfile(Integer userId, UserUpdateRequest request);

    List<User> findAll();
    Optional<User> findByIdWithRoles(Integer id);
    User updateUserRoles(Integer userId, List<String> roleNames);
    User toggleUserActive(Integer userId, boolean active, String reason);
}
