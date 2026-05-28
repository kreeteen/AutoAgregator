package ru.vsu.cs.edportal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.vsu.cs.edportal.model.VehicleCar;

import java.util.List;
import java.util.Optional;

public interface VehicleCarRepository extends JpaRepository<VehicleCar, Integer>, JpaSpecificationExecutor<VehicleCar> {

    @Query("SELECT v FROM VehicleCar v LEFT JOIN FETCH v.projectTag LEFT JOIN FETCH v.carBrand LEFT JOIN FETCH v.carModel WHERE v.user.id = :userId")
    List<VehicleCar> findByUserIdWithAssociations(@Param("userId") Integer userId);

    @Query("SELECT v FROM VehicleCar v JOIN FETCH v.projectTag LEFT JOIN FETCH v.user LEFT JOIN FETCH v.carBrand LEFT JOIN FETCH v.carModel LEFT JOIN FETCH v.carGeneration LEFT JOIN FETCH v.region ORDER BY v.createdAt DESC")
    Page<VehicleCar> findAllWithTag(Pageable pageable);

    @Query("SELECT DISTINCT v FROM VehicleCar v JOIN FETCH v.projectTag LEFT JOIN FETCH v.user ORDER BY v.createdAt DESC")
    List<VehicleCar> findAllWithAssociations();

    @Query("SELECT v FROM VehicleCar v JOIN FETCH v.projectTag LEFT JOIN FETCH v.user LEFT JOIN FETCH v.selectedMods WHERE v.id = :id")
    Optional<VehicleCar> findByIdWithAssociations(@Param("id") Integer id);

    @Query("SELECT v FROM VehicleCar v JOIN FETCH v.projectTag LEFT JOIN FETCH v.user LEFT JOIN FETCH v.carBrand LEFT JOIN FETCH v.carModel LEFT JOIN FETCH v.carGeneration LEFT JOIN FETCH v.region WHERE v.id = :id")
    Optional<VehicleCar> findByIdWithAll(@Param("id") Integer id);
}
