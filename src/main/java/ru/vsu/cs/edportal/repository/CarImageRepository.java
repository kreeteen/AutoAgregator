package ru.vsu.cs.edportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.edportal.model.CarImage;

import java.util.List;

public interface CarImageRepository extends JpaRepository<CarImage, Integer> {
    List<CarImage> findByCarIdOrderBySortOrderAsc(Integer carId);
    void deleteByCarId(Integer carId);
}
