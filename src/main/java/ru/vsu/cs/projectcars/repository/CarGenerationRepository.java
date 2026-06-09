package ru.vsu.cs.projectcars.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.projectcars.model.CarGeneration;

import java.util.List;

public interface CarGenerationRepository extends JpaRepository<CarGeneration, Integer> {
    List<CarGeneration> findByModelIdOrderByNameAsc(Integer modelId);
}
