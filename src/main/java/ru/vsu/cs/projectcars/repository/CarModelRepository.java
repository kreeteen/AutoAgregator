package ru.vsu.cs.projectcars.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.projectcars.model.CarModel;

import java.util.List;

public interface CarModelRepository extends JpaRepository<CarModel, Integer> {
    List<CarModel> findByBrandIdOrderByNameAsc(Integer brandId);
}
