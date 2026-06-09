package ru.vsu.cs.projectcars.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.projectcars.model.RussianRegion;

import java.util.List;

public interface RussianRegionRepository extends JpaRepository<RussianRegion, Integer> {
    List<RussianRegion> findAllByOrderByNameAsc();
}
