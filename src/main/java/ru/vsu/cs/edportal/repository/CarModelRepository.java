package ru.vsu.cs.edportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.edportal.model.CarModel;

import java.util.List;

public interface CarModelRepository extends JpaRepository<CarModel, Integer> {
    List<CarModel> findByBrandIdOrderByNameAsc(Integer brandId);
}
