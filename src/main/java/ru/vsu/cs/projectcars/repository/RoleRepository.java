package ru.vsu.cs.projectcars.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.projectcars.model.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);
}
