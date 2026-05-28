package ru.vsu.cs.edportal.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.vsu.cs.edportal.dto.CarFilter;
import ru.vsu.cs.edportal.model.VehicleCar;

import java.util.List;
import java.util.Optional;

public interface VehicleCarService {
    List<VehicleCar> findAll();
    Page<VehicleCar> findAllPaged(Pageable pageable);
    Optional<VehicleCar> findById(Integer id);
    Optional<VehicleCar> findByIdWithAssociations(Integer id);
    Optional<VehicleCar> findByIdWithAll(Integer id);
    List<VehicleCar> findByFilter(CarFilter filter);
    Page<VehicleCar> findByFilterPaged(CarFilter filter, Pageable pageable);
    Page<VehicleCar> findByFilterPagedWithInit(CarFilter filter, Pageable pageable);
    List<VehicleCar> searchByMod(String modName);
    VehicleCar save(VehicleCar car);
    VehicleCar update(Integer id, VehicleCar car);
    void delete(Integer id);
}
