package ru.vsu.cs.projectcars.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.projectcars.model.ModsCategory;
import ru.vsu.cs.projectcars.model.ProjectTag;

import java.util.List;

public interface ModsCategoryRepository extends JpaRepository<ModsCategory, Integer> {
    List<ModsCategory> findByProjectTag(ProjectTag projectTag);
}
