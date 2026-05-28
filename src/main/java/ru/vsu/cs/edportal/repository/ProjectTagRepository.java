package ru.vsu.cs.edportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.edportal.model.ProjectTag;

public interface ProjectTagRepository extends JpaRepository<ProjectTag, Integer> {
    ProjectTag findByName(String name);
}
