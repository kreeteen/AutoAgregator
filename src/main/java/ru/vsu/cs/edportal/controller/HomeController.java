package ru.vsu.cs.edportal.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.vsu.cs.edportal.model.VehicleCar;
import ru.vsu.cs.edportal.repository.ProjectTagRepository;
import ru.vsu.cs.edportal.repository.VehicleCarRepository;

import java.util.List;

@Controller
public class HomeController {

    private final VehicleCarRepository carRepository;
    private final ProjectTagRepository tagRepository;

    public HomeController(VehicleCarRepository carRepository,
                          ProjectTagRepository tagRepository) {
        this.carRepository = carRepository;
        this.tagRepository = tagRepository;
    }

    @GetMapping("/")
    @Transactional(readOnly = true)
    public String index(Model model) {
        List<VehicleCar> recent = carRepository.findAllWithTag(
                PageRequest.of(0, 6))
                .getContent();
        recent.forEach(car -> {
            org.hibernate.Hibernate.initialize(car.getImages());
            java.util.List<?> mods = car.getSelectedMods();
            if (mods != null) org.hibernate.Hibernate.initialize(mods);
        });
        model.addAttribute("totalListings", carRepository.count());
        model.addAttribute("totalTags", tagRepository.count());
        model.addAttribute("recentCars", recent);
        return "index";
    }
}
