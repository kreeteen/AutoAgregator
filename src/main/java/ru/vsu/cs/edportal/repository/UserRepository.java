package ru.vsu.cs.edportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.edportal.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
