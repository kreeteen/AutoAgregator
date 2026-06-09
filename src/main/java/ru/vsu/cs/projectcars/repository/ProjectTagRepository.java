package ru.vsu.cs.projectcars.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.projectcars.model.ProjectTag;

public interface ProjectTagRepository extends JpaRepository<ProjectTag, Integer> {
    ProjectTag findByName(String name);
}
