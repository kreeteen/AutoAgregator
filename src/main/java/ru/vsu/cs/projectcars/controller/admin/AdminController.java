package ru.vsu.cs.projectcars.controller.admin;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.projectcars.model.*;
import ru.vsu.cs.projectcars.repository.*;
import ru.vsu.cs.projectcars.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final VehicleCarRepository carRepository;
    private final ProjectTagRepository tagRepository;
    private final ModsCategoryRepository modsRepository;
    private final RoleRepository roleRepository;

    public AdminController(UserService userService,
                           VehicleCarRepository carRepository,
                           ProjectTagRepository tagRepository,
                           ModsCategoryRepository modsRepository,
                           RoleRepository roleRepository) {
        this.userService = userService;
        this.carRepository = carRepository;
        this.tagRepository = tagRepository;
        this.modsRepository = modsRepository;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("userCount", userService.findAll().size());
        model.addAttribute("carCount", carRepository.count());
        model.addAttribute("tagCount", tagRepository.count());
        model.addAttribute("modCount", modsRepository.count());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    @Transactional(readOnly = true)
    public String users(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/users";
    }

    @PostMapping("/users/{id}/roles")
    public String updateRoles(@PathVariable Integer id,
                              @RequestParam(value = "roles", required = false) List<String> roleNames) {
        if (roleNames == null) roleNames = List.of();
        userService.updateUserRoles(id, roleNames);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/ban")
    public String banUser(@PathVariable Integer id,
                          @RequestParam(defaultValue = "Нарушение правил") String reason) {
        userService.toggleUserActive(id, false, reason);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/unban")
    public String unbanUser(@PathVariable Integer id) {
        userService.toggleUserActive(id, true, null);
        return "redirect:/admin/users";
    }

    @GetMapping("/cars")
    @Transactional(readOnly = true)
    public String cars(Model model) {
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
        return "admin/cars";
    }

    @PostMapping("/cars/{id}/delete")
    public String deleteCar(@PathVariable Integer id) {
        carRepository.deleteById(id);
        return "redirect:/admin/cars";
    }

    @GetMapping("/tags")
    @Transactional(readOnly = true)
    public String tags(Model model) {
        List<ProjectTag> allTags = tagRepository.findAll();
        allTags.forEach(t -> {
            Hibernate.initialize(t.getModsCategories());
            t.setModsCategories(new java.util.ArrayList<>(t.getModsCategories()));
        });
        model.addAttribute("tags", allTags);
        return "admin/tags";
    }

    @GetMapping("/mods")
    @Transactional(readOnly = true)
    public String mods(Model model) {
        List<ProjectTag> allTags = tagRepository.findAll();
        allTags.forEach(t -> {
            Hibernate.initialize(t.getModsCategories());
            t.setModsCategories(new java.util.ArrayList<>(t.getModsCategories()));
        });
        model.addAttribute("tags", allTags);
        return "admin/mods";
    }

    @PostMapping("/tags")
    public String createModFromTags(@RequestParam Integer tagId, @RequestParam String name) {
        ProjectTag tag = tagRepository.findById(tagId).orElseThrow();
        ModsCategory mod = new ModsCategory();
        mod.setProjectTag(tag);
        mod.setName(name);
        modsRepository.save(mod);
        return "redirect:/admin/tags";
    }

    @PostMapping("/tags/{id}/delete")
    public String deleteTag(@PathVariable Integer id) {
        tagRepository.deleteById(id);
        return "redirect:/admin/tags";
    }

    @PostMapping("/mods")
    public String createTagFromMods(@RequestParam String name,
                                    @RequestParam(required = false) String description) {
        ProjectTag tag = new ProjectTag(name);
        tag.setDescription(description);
        tagRepository.save(tag);
        return "redirect:/admin/mods";
    }

    @PostMapping("/mods/{id}/delete")
    public String deleteMod(@PathVariable Integer id) {
        modsRepository.deleteById(id);
        return "redirect:/admin/tags";
    }
}
