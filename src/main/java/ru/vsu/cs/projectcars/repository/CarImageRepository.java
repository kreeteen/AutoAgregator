package ru.vsu.cs.projectcars.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.projectcars.model.CarImage;

import java.util.List;

public interface CarImageRepository extends JpaRepository<CarImage, Integer> {
    List<CarImage> findByCarIdOrderBySortOrderAsc(Integer carId);
    void deleteByCarId(Integer carId);
}
