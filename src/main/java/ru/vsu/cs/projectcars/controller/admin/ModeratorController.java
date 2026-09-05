package ru.vsu.cs.projectcars.controller.admin;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.projectcars.model.VehicleCar;
import ru.vsu.cs.projectcars.repository.VehicleCarRepository;
import ru.vsu.cs.projectcars.service.FileStorageService;

import java.util.List;

@Controller
@RequestMapping("/moderate")
public class ModeratorController {

    private final VehicleCarRepository carRepository;
    private final FileStorageService fileStorageService;

    public ModeratorController(VehicleCarRepository carRepository,
                               FileStorageService fileStorageService) {
        this.carRepository = carRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public String listings(Model model) {
        List<VehicleCar> all = carRepository.findAllWithAssociations();
        all.forEach(c -> {
            Hibernate.initialize(c.getUser());
            Hibernate.initialize(c.getProjectTag());
            Hibernate.initialize(c.getCarBrand());
            Hibernate.initialize(c.getCarModel());
            Hibernate.initialize(c.getCarGeneration());
            Hibernate.initialize(c.getRegion());
            Hibernate.initialize(c.getSelectedMods());
            c.setSelectedMods(new java.util.ArrayList<>(c.getSelectedMods()));
        });
        model.addAttribute("cars", all);
        return "moderate/listings";
    }

    @PostMapping("/cars/{id}/delete")
    public String deleteCar(@PathVariable Integer id) {
        carRepository.findByIdWithAll(id).ifPresent(car -> {
            fileStorageService.deleteImages(car.getImages());
            carRepository.delete(car);
        });
        return "redirect:/moderate";
    }
}
