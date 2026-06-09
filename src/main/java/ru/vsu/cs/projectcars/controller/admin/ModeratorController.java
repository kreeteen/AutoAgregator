package ru.vsu.cs.projectcars.controller.admin;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.projectcars.model.VehicleCar;
import ru.vsu.cs.projectcars.repository.VehicleCarRepository;

import java.util.List;

@Controller
@RequestMapping("/moderate")
public class ModeratorController {

    private final VehicleCarRepository carRepository;

    public ModeratorController(VehicleCarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public String listings(Model model) {
        List<VehicleCar> all = carRepository.findAllWithAssociations();
        all.forEach(c -> Hibernate.initialize(c.getUser()));
        model.addAttribute("cars", all);
        return "moderate/listings";
    }

    @PostMapping("/cars/{id}/delete")
    public String deleteCar(@PathVariable Integer id) {
        carRepository.deleteById(id);
        return "redirect:/moderate";
    }
}
