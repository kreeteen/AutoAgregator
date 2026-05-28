package ru.vsu.cs.edportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.edportal.model.RussianRegion;

import java.util.List;

public interface RussianRegionRepository extends JpaRepository<RussianRegion, Integer> {
    List<RussianRegion> findAllByOrderByNameAsc();
}
