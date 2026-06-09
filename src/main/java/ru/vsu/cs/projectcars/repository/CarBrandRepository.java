package ru.vsu.cs.projectcars.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.projectcars.model.CarBrand;

import java.util.List;
import java.util.Optional;

public interface CarBrandRepository extends JpaRepository<CarBrand, Integer> {
    List<CarBrand> findAllByOrderByNameAsc();
    Optional<CarBrand> findByName(String name);
}
