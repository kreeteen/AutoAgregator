package ru.vsu.cs.edportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.edportal.model.ModsCategory;
import ru.vsu.cs.edportal.model.ProjectTag;

import java.util.List;

public interface ModsCategoryRepository extends JpaRepository<ModsCategory, Integer> {
    List<ModsCategory> findByProjectTag(ProjectTag projectTag);
}
