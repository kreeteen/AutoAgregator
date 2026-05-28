package ru.vsu.cs.edportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.edportal.model.CarGeneration;

import java.util.List;

public interface CarGenerationRepository extends JpaRepository<CarGeneration, Integer> {
    List<CarGeneration> findByModelIdOrderByNameAsc(Integer modelId);
}
