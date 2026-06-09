package ru.vsu.cs.projectcars.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.*;
import jakarta.persistence.criteria.Subquery;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vsu.cs.projectcars.dto.CarFilter;
import ru.vsu.cs.projectcars.model.CarImage;
import ru.vsu.cs.projectcars.model.ModsCategory;
import ru.vsu.cs.projectcars.model.VehicleCar;
import ru.vsu.cs.projectcars.repository.VehicleCarRepository;
import ru.vsu.cs.projectcars.service.FileStorageService;
import ru.vsu.cs.projectcars.service.VehicleCarService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class VehicleCarServiceImpl implements VehicleCarService {

    private final VehicleCarRepository repository;
    private final FileStorageService fileStorageService;

    public VehicleCarServiceImpl(VehicleCarRepository repository,
                                  FileStorageService fileStorageService) {
        this.repository = repository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleCar> findAll() {
        return repository.findAllWithAssociations();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehicleCar> findAllPaged(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VehicleCar> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VehicleCar> findByIdWithAssociations(Integer id) {
        Optional<VehicleCar> opt = repository.findByIdWithAssociations(id);
        opt.ifPresent(this::initializeAssociations);
        return opt;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VehicleCar> findByIdWithAll(Integer id) {
        Optional<VehicleCar> opt = repository.findByIdWithAll(id);
        opt.ifPresent(this::initializeAssociations);
        return opt;
    }

    private void initializeAssociations(VehicleCar car) {
        Hibernate.initialize(car.getProjectTag());
        Hibernate.initialize(car.getUser());
        Hibernate.initialize(car.getCarBrand());
        Hibernate.initialize(car.getCarModel());
        Hibernate.initialize(car.getCarGeneration());
        Hibernate.initialize(car.getRegion());
        Hibernate.initialize(car.getImages());
        List<ModsCategory> modsList = car.getSelectedMods();
        if (modsList == null) {
            car.setSelectedMods(new ArrayList<>());
        } else {
            if (modsList instanceof org.hibernate.collection.spi.PersistentCollection) {
                Hibernate.initialize(modsList);
                car.setSelectedMods(new ArrayList<>(modsList));
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleCar> findByFilter(CarFilter filter) {
        return repository.findAll(buildSpec(filter));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehicleCar> findByFilterPaged(CarFilter filter, Pageable pageable) {
        return repository.findAll(buildSpec(filter), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehicleCar> findByFilterPagedWithInit(CarFilter filter, Pageable pageable) {
        Page<VehicleCar> page = findByFilterPaged(filter, pageable);
        page.forEach(car -> {
            if (car == null) return;
            Hibernate.initialize(car.getProjectTag());
            Hibernate.initialize(car.getUser());
            Hibernate.initialize(car.getCarBrand());
            Hibernate.initialize(car.getCarModel());
            Hibernate.initialize(car.getCarGeneration());
            Hibernate.initialize(car.getRegion());
            Hibernate.initialize(car.getImages());
            List<ModsCategory> mods = car.getSelectedMods();
            if (mods != null) {
                Hibernate.initialize(mods);
                car.setSelectedMods(new ArrayList<>(mods));
            }
        });
        return page;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleCar> searchByMod(String modName) {
        return repository.findAllWithAssociations().stream()
                .filter(car -> car.getSelectedMods().stream()
                        .anyMatch(m -> m.getName().toLowerCase().contains(modName.toLowerCase())))
                .collect(Collectors.toList());
    }

    @Override
    public VehicleCar save(VehicleCar car) {
        if (car.getImages() != null) {
            car.getImages().forEach(img -> img.setCar(car));
        }
        return repository.save(car);
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(VehicleCarServiceImpl.class);

    @Override
    public VehicleCar update(Integer id, VehicleCar updated) {
        log.info(">>> update({}) called: price={}, city={}, manufactureYear={}, modsCount={}, engineType={}",
                id, updated.getPrice(), updated.getCity(), updated.getManufactureYear(),
                updated.getSelectedMods() != null ? updated.getSelectedMods().size() : 0,
                updated.getEngineType());
        VehicleCar existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Авто не найдено: " + id));

        log.info("    existing before: price={}, city={}, year={}, engineType={}",
                existing.getPrice(), existing.getCity(), existing.getManufactureYear(), existing.getEngineType());

        if (updated.getImages() != null && !updated.getImages().isEmpty()) {
            fileStorageService.deleteImages(existing.getImages());
            existing.getImages().clear();
            updated.getImages().forEach(img -> {
                img.setCar(existing);
                existing.getImages().add(img);
            });
        }

        existing.setManufactureYear(updated.getManufactureYear());
        existing.setPrice(updated.getPrice());
        existing.setCity(updated.getCity());
        existing.setRegion(updated.getRegion());
        existing.setProjectTag(updated.getProjectTag());
        existing.setCarBrand(updated.getCarBrand());
        existing.setCarModel(updated.getCarModel());
        existing.setCarGeneration(updated.getCarGeneration());
        existing.setDescription(updated.getDescription());
        existing.setMileageKm(updated.getMileageKm());
        existing.setMileageHours(updated.getMileageHours());
        existing.setEngineType(updated.getEngineType());
        existing.setEngineDisplacement(updated.getEngineDisplacement());
        existing.setEnginePower(updated.getEnginePower());
        existing.setBodyType(updated.getBodyType());
        existing.setDamaged(updated.isDamaged());
        existing.setSteeringSide(updated.getSteeringSide());
        existing.setRegistered(updated.isRegistered());
        existing.getSelectedMods().clear();
        if (updated.getSelectedMods() != null) {
            existing.getSelectedMods().addAll(updated.getSelectedMods());
        }
        VehicleCar saved = repository.save(existing);
        log.info("<<< update({}) done: price={}, city={}, year={}, engineType={}, modsCount={}",
                id, saved.getPrice(), saved.getCity(), saved.getManufactureYear(), saved.getEngineType(),
                saved.getSelectedMods() != null ? saved.getSelectedMods().size() : 0);
        return saved;
    }

    @Override
    public void delete(Integer id) {
        Optional<VehicleCar> opt = repository.findByIdWithAll(id);
        if (opt.isPresent()) {
            fileStorageService.deleteImages(opt.get().getImages());
        }
        repository.deleteById(id);
    }

    private Specification<VehicleCar> buildSpec(CarFilter filter) {
        return (Root<VehicleCar> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter == null) return cb.conjunction();

            if (filter.getProjectTagId() != null) {
                predicates.add(cb.equal(root.get("projectTag").get("id"), filter.getProjectTagId()));
            }
            if (filter.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), filter.getMinPrice()));
            }
            if (filter.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), filter.getMaxPrice()));
            }
            if (filter.getCity() != null && !filter.getCity().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("city")), "%" + filter.getCity().toLowerCase() + "%"));
            }
            if (filter.getMinYear() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("manufactureYear"), filter.getMinYear()));
            }
            if (filter.getMaxYear() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("manufactureYear"), filter.getMaxYear()));
            }

            if (filter.getBrandId() != null) {
                predicates.add(cb.equal(root.get("carBrand").get("id"), filter.getBrandId()));
            }
            if (filter.getModelId() != null) {
                predicates.add(cb.equal(root.get("carModel").get("id"), filter.getModelId()));
            }
            if (filter.getGenerationId() != null) {
                predicates.add(cb.equal(root.get("carGeneration").get("id"), filter.getGenerationId()));
            }
            if (filter.getRegionId() != null) {
                predicates.add(cb.equal(root.get("region").get("id"), filter.getRegionId()));
            }
            if (filter.getMinMileageKm() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("mileageKm"), filter.getMinMileageKm()));
            }
            if (filter.getMaxMileageKm() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("mileageKm"), filter.getMaxMileageKm()));
            }
            if (filter.getMinMileageHours() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("mileageHours"), filter.getMinMileageHours()));
            }
            if (filter.getMaxMileageHours() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("mileageHours"), filter.getMaxMileageHours()));
            }
            if (filter.getEngineType() != null && !filter.getEngineType().isBlank()) {
                predicates.add(cb.equal(root.get("engineType"), filter.getEngineType()));
            }
            if (filter.getMinEngineDisplacement() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("engineDisplacement"), filter.getMinEngineDisplacement()));
            }
            if (filter.getMaxEngineDisplacement() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("engineDisplacement"), filter.getMaxEngineDisplacement()));
            }
            if (filter.getMinEnginePower() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("enginePower"), filter.getMinEnginePower()));
            }
            if (filter.getMaxEnginePower() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("enginePower"), filter.getMaxEnginePower()));
            }
            if (filter.getBodyType() != null && !filter.getBodyType().isBlank()) {
                predicates.add(cb.equal(root.get("bodyType"), filter.getBodyType()));
            }
            if (filter.getDamaged() != null) {
                predicates.add(cb.equal(root.get("damaged"), filter.getDamaged()));
            }
            if (filter.getSteeringSide() != null && !filter.getSteeringSide().isBlank()) {
                predicates.add(cb.equal(root.get("steeringSide"), filter.getSteeringSide()));
            }
            if (filter.getRegistered() != null) {
                predicates.add(cb.equal(root.get("registered"), filter.getRegistered()));
            }

            if (filter.getQuery() != null && !filter.getQuery().isBlank()) {
                String q = "%" + filter.getQuery().toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("carBrand").get("name")), q),
                    cb.like(cb.lower(root.get("carModel").get("name")), q),
                    cb.like(cb.lower(root.get("description")), q)
                ));
            }

            if (filter.getModCategoryIds() != null && !filter.getModCategoryIds().isEmpty()) {
                Subquery<Integer> subquery = query.subquery(Integer.class);
                Root<VehicleCar> subRoot = subquery.from(VehicleCar.class);
                Join<VehicleCar, ModsCategory> modsJoin = subRoot.join("selectedMods");
                subquery.select(subRoot.get("id")).distinct(true);
                subquery.where(modsJoin.get("id").in(filter.getModCategoryIds()));
                predicates.add(cb.in(root.get("id")).value(subquery));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
