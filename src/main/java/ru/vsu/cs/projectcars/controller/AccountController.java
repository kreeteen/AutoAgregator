package ru.vsu.cs.projectcars.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.projectcars.dto.CarFilter;
import ru.vsu.cs.projectcars.dto.UserUpdateRequest;
import ru.vsu.cs.projectcars.model.VehicleCar;
import ru.vsu.cs.projectcars.repository.*;
import ru.vsu.cs.projectcars.security.JwtPrincipal;
import ru.vsu.cs.projectcars.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/account")
public class AccountController {

    private final VehicleCarRepository carRepository;
    private final UserService userService;
    private final CarBrandRepository brandRepository;
    private final CarModelRepository modelRepository;
    private final CarGenerationRepository generationRepository;

    public AccountController(VehicleCarRepository carRepository,
                             UserService userService,
                             CarBrandRepository brandRepository,
                             CarModelRepository modelRepository,
                             CarGenerationRepository generationRepository) {
        this.carRepository = carRepository;
        this.userService = userService;
        this.brandRepository = brandRepository;
        this.modelRepository = modelRepository;
        this.generationRepository = generationRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public String account(@AuthenticationPrincipal JwtPrincipal principal,
                          CarFilter filter, Model model) {
        if (principal == null) return "redirect:/login";

        Integer userId = principal.getUserId();
        var user = userService.findById(userId).orElse(null);
        if (user == null) return "redirect:/login";

        List<VehicleCar> allUserCars = carRepository.findByUserIdWithAssociations(userId);
        allUserCars.forEach(c -> {
            org.hibernate.Hibernate.initialize(c.getImages());
            java.util.List<?> mods = c.getSelectedMods();
            if (mods != null) org.hibernate.Hibernate.initialize(mods);
        });

        List<VehicleCar> filteredCars = allUserCars.stream()
                .filter(c -> c != null)
                .filter(c -> filter.getBrandId() == null || (c.getCarBrand() != null && c.getCarBrand().getId().equals(filter.getBrandId())))
                .filter(c -> filter.getModelId() == null || (c.getCarModel() != null && c.getCarModel().getId().equals(filter.getModelId())))
                .filter(c -> filter.getMinPrice() == null || (c.getPrice() != null && c.getPrice().compareTo(filter.getMinPrice()) >= 0))
                .filter(c -> filter.getMaxPrice() == null || (c.getPrice() != null && c.getPrice().compareTo(filter.getMaxPrice()) <= 0))
                .collect(Collectors.toList());

        long totalViews = allUserCars.stream()
                .mapToLong(c -> c.getViewsCount() != null ? c.getViewsCount() : 0)
                .sum();

        model.addAttribute("profileUser", user);
        model.addAttribute("cars", filteredCars);
        model.addAttribute("filter", filter);
        model.addAttribute("totalViews", totalViews);
        model.addAttribute("totalCars", allUserCars.size());
        model.addAttribute("filteredCars", filteredCars.size());
        model.addAttribute("brands", brandRepository.findAllByOrderByNameAsc());

        if (filter.getBrandId() != null) {
            model.addAttribute("models", modelRepository.findByBrandIdOrderByNameAsc(filter.getBrandId()));
        }
        if (filter.getModelId() != null) {
            model.addAttribute("generations", generationRepository.findByModelIdOrderByNameAsc(filter.getModelId()));
        }

        return "account";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal JwtPrincipal principal,
                                @ModelAttribute UserUpdateRequest request) {
        if (principal == null) return "redirect:/login";
        userService.updateProfile(principal.getUserId(), request);
        return "redirect:/account";
    }
}
