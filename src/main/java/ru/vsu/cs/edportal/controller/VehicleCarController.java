package ru.vsu.cs.edportal.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.vsu.cs.edportal.dto.CarFilter;
import ru.vsu.cs.edportal.dto.ValidationResult;
import ru.vsu.cs.edportal.model.*;
import ru.vsu.cs.edportal.repository.*;
import ru.vsu.cs.edportal.security.UserContext;
import ru.vsu.cs.edportal.service.PhoneMaskingService;
import ru.vsu.cs.edportal.service.UserService;
import ru.vsu.cs.edportal.service.VehicleCarService;
import ru.vsu.cs.edportal.service.ValidationService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cars")
public class VehicleCarController {

    private final VehicleCarService carService;
    private final ProjectTagRepository tagRepository;
    private final ModsCategoryRepository modsRepository;
    private final ValidationService validationService;
    private final PhoneMaskingService phoneMaskingService;
    private final UserService userService;
    private final UserContext userContext;
    private final CarBrandRepository brandRepository;
    private final CarModelRepository modelRepository;
    private final CarGenerationRepository generationRepository;
    private final RussianRegionRepository regionRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public VehicleCarController(VehicleCarService carService,
                                ProjectTagRepository tagRepository,
                                ModsCategoryRepository modsRepository,
                                ValidationService validationService,
                                PhoneMaskingService phoneMaskingService,
                                UserService userService,
                                UserContext userContext,
                                CarBrandRepository brandRepository,
                                CarModelRepository modelRepository,
                                CarGenerationRepository generationRepository,
                                RussianRegionRepository regionRepository) {
        this.carService = carService;
        this.tagRepository = tagRepository;
        this.modsRepository = modsRepository;
        this.validationService = validationService;
        this.phoneMaskingService = phoneMaskingService;
        this.userService = userService;
        this.userContext = userContext;
        this.brandRepository = brandRepository;
        this.modelRepository = modelRepository;
        this.generationRepository = generationRepository;
        this.regionRepository = regionRepository;
    }

    @GetMapping
    public String list(CarFilter filter,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       Model model) {
        Page<VehicleCar> carPage = carService.findByFilterPagedWithInit(filter, PageRequest.of(page, size));
        List<VehicleCar> cars = carPage.getContent().stream()
                .filter(c -> c != null)
                .collect(Collectors.toList());
        model.addAttribute("carPage", carPage);
        model.addAttribute("cars", cars);
        model.addAttribute("filter", filter);
        model.addAttribute("tags", tagRepository.findAll());
        model.addAttribute("regions", regionRepository.findAllByOrderByNameAsc());
        model.addAttribute("brands", brandRepository.findAllByOrderByNameAsc());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", carPage.getTotalPages());

        if (filter.getBrandId() != null) {
            model.addAttribute("models", modelRepository.findByBrandIdOrderByNameAsc(filter.getBrandId()));
        }
        if (filter.getModelId() != null) {
            model.addAttribute("generations", generationRepository.findByModelIdOrderByNameAsc(filter.getModelId()));
        }

        return "cars/list";
    }

    @GetMapping("/tag/{tagId}")
    @Transactional(readOnly = true)
    public String byTag(@PathVariable Integer tagId,
                        CarFilter filter,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size,
                        Model model) {
        filter.setProjectTagId(tagId);
        Page<VehicleCar> carPage = carService.findByFilterPagedWithInit(filter, PageRequest.of(page, size));
        List<VehicleCar> cars = carPage.getContent().stream()
                .filter(c -> c != null)
                .collect(Collectors.toList());
        model.addAttribute("carPage", carPage);
        model.addAttribute("cars", cars);
        model.addAttribute("filter", filter);
        model.addAttribute("brands", brandRepository.findAllByOrderByNameAsc());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", carPage.getTotalPages());
        model.addAttribute("currentTag", tagRepository.findById(tagId).orElse(null));

        ProjectTag tag = tagRepository.findById(tagId).orElse(null);
        if (tag != null) {
            model.addAttribute("tagMods", modsRepository.findByProjectTag(tag));
        }

        if (filter.getBrandId() != null) {
            model.addAttribute("models", modelRepository.findByBrandIdOrderByNameAsc(filter.getBrandId()));
        }
        if (filter.getModelId() != null) {
            model.addAttribute("generations", generationRepository.findByModelIdOrderByNameAsc(filter.getModelId()));
        }

        return "cars/by-tag";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Optional<VehicleCar> opt = carService.findByIdWithAll(id);
        if (opt.isEmpty()) return "redirect:/cars";
        VehicleCar car = opt.get();
        model.addAttribute("car", car);

        List<ModsCategory> mods = car.getSelectedMods();
        if (mods != null && !mods.isEmpty()) {
            model.addAttribute("carModsHtml", mods.stream()
                    .map(m -> "<span class=\"badge bg-info text-dark\">" + m.getName() + "</span>")
                    .collect(Collectors.joining(" ")));
        } else {
            model.addAttribute("carModsHtml", "");
        }

        if (userContext.isAuthenticated()) {
            String fullPhone = userService.getPhoneNumber(car.getUser().getId());
            model.addAttribute("sellerPhoneMasked", phoneMaskingService.mask(fullPhone));
            model.addAttribute("sellerPhoneFull", fullPhone);
        }
        return "cars/detail";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("car", new VehicleCar());
        model.addAttribute("tags", tagRepository.findAll());
        model.addAttribute("brands", brandRepository.findAllByOrderByNameAsc());
        model.addAttribute("regions", regionRepository.findAllByOrderByNameAsc());
        return "cars/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("car") VehicleCar car, BindingResult result,
                         @RequestParam(value = "modIds", required = false) List<Integer> modIds,
                         @RequestParam(value = "images", required = false) List<MultipartFile> imageFiles,
                         @RequestParam(value = "brandId", required = false) Integer brandId,
                         @RequestParam(value = "modelId", required = false) Integer modelId,
                         @RequestParam(value = "genId", required = false) Integer genId,
                         @RequestParam(value = "regionId", required = false) Integer regionId,
                         @RequestParam(value = "tagId", required = false) Integer tagId,
                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("tags", tagRepository.findAll());
            model.addAttribute("brands", brandRepository.findAllByOrderByNameAsc());
            model.addAttribute("regions", regionRepository.findAllByOrderByNameAsc());
            return "cars/form";
        }
        if (!userContext.isAuthenticated()) return "redirect:/login";

        userService.findById(userContext.getUserId()).ifPresent(car::setUser);
        resolveEntityRefs(car, brandId, modelId, genId, regionId, tagId, modIds);
        handleImages(car, imageFiles);

        ValidationResult v = validationService.validate(car);
        if (v.hasErrors()) {
            model.addAttribute("tags", tagRepository.findAll());
            model.addAttribute("brands", brandRepository.findAllByOrderByNameAsc());
            model.addAttribute("regions", regionRepository.findAllByOrderByNameAsc());
            model.addAttribute("validationErrors", v.getErrors());
            model.addAttribute("validationWarnings", v.getWarnings());
            return "cars/form";
        }
        return "redirect:/cars/" + carService.save(car).getId();
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        Optional<VehicleCar> opt = carService.findByIdWithAll(id);
        if (opt.isEmpty()) return "redirect:/cars";
        model.addAttribute("car", opt.get());
        model.addAttribute("tags", tagRepository.findAll());
        model.addAttribute("brands", brandRepository.findAllByOrderByNameAsc());
        model.addAttribute("regions", regionRepository.findAllByOrderByNameAsc());
        VehicleCar car = opt.get();
        if (car.getCarBrand() != null) {
            model.addAttribute("models", modelRepository.findByBrandIdOrderByNameAsc(car.getCarBrand().getId()));
        }
        if (car.getCarModel() != null) {
            model.addAttribute("generations", generationRepository.findByModelIdOrderByNameAsc(car.getCarModel().getId()));
        }
        return "cars/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id, @Valid @ModelAttribute("car") VehicleCar car,
                         BindingResult result,
                         @RequestParam(value = "modIds", required = false) List<Integer> modIds,
                         @RequestParam(value = "images", required = false) List<MultipartFile> imageFiles,
                         @RequestParam(value = "brandId", required = false) Integer brandId,
                         @RequestParam(value = "modelId", required = false) Integer modelId,
                         @RequestParam(value = "genId", required = false) Integer genId,
                         @RequestParam(value = "regionId", required = false) Integer regionId,
                         @RequestParam(value = "tagId", required = false) Integer tagId,
                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("tags", tagRepository.findAll());
            model.addAttribute("brands", brandRepository.findAllByOrderByNameAsc());
            model.addAttribute("regions", regionRepository.findAllByOrderByNameAsc());
            return "cars/form";
        }
        resolveEntityRefs(car, brandId, modelId, genId, regionId, tagId, modIds);
        handleImages(car, imageFiles);

        ValidationResult v = validationService.validate(car);
        if (v.hasErrors()) {
            model.addAttribute("tags", tagRepository.findAll());
            model.addAttribute("brands", brandRepository.findAllByOrderByNameAsc());
            model.addAttribute("regions", regionRepository.findAllByOrderByNameAsc());
            model.addAttribute("validationErrors", v.getErrors());
            model.addAttribute("validationWarnings", v.getWarnings());
            return "cars/form";
        }
        carService.update(id, car);
        return "redirect:/cars/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {
        if (!userContext.isAuthenticated()) return "redirect:/login";
        Optional<VehicleCar> car = carService.findByIdWithAll(id);
        if (car.isEmpty()) return "redirect:/cars";
        if (!car.get().getUser().getId().equals(userContext.getUserId())) return "redirect:/cars";
        carService.delete(id);
        return "redirect:/cars";
    }

    @GetMapping("/search")
    public String search(@RequestParam("q") String query, Model model) {
        List<VehicleCar> results = carService.searchByMod(query);
        model.addAttribute("cars", results);
        model.addAttribute("searchQuery", query);
        model.addAttribute("tags", tagRepository.findAll());
        model.addAttribute("brands", brandRepository.findAllByOrderByNameAsc());
        model.addAttribute("regions", regionRepository.findAllByOrderByNameAsc());
        return "cars/list";
    }

    @GetMapping("/api/models")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getModels(@RequestParam Integer brandId) {
        List<CarModel> models = modelRepository.findByBrandIdOrderByNameAsc(brandId);
        List<Map<String, Object>> result = models.stream().map(m -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", m.getId());
            map.put("name", m.getName());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/generations")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getGenerations(@RequestParam Integer modelId) {
        List<CarGeneration> gens = generationRepository.findByModelIdOrderByNameAsc(modelId);
        List<Map<String, Object>> result = gens.stream().map(g -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", g.getId());
            map.put("name", g.getName());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    private void resolveEntityRefs(VehicleCar car, Integer brandId, Integer modelId, Integer genId,
                                   Integer regionId, Integer tagId, List<Integer> modIds) {
        if (brandId != null) brandRepository.findById(brandId).ifPresent(car::setCarBrand);
        if (modelId != null) modelRepository.findById(modelId).ifPresent(car::setCarModel);
        if (genId != null) generationRepository.findById(genId).ifPresent(car::setCarGeneration);
        if (regionId != null) regionRepository.findById(regionId).ifPresent(car::setRegion);
        if (tagId != null) tagRepository.findById(tagId).ifPresent(car::setProjectTag);
        if (tagId != null && modIds != null) {
            car.setSelectedMods(modsRepository.findAllById(modIds));
        }
    }

    private void handleImages(VehicleCar car, List<MultipartFile> imageFiles) {
        if (imageFiles == null || imageFiles.isEmpty()) return;
        List<CarImage> images = new ArrayList<>();
        int order = 0;
        for (MultipartFile file : imageFiles) {
            if (file.isEmpty()) continue;
            try {
                String carDir = uploadDir + "/temp/" + UUID.randomUUID();
                Path dirPath = Paths.get(carDir);
                Files.createDirectories(dirPath);
                String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = dirPath.resolve(filename);
                Files.copy(file.getInputStream(), filePath);

                CarImage img = new CarImage();
                img.setCar(car);
                img.setFilePath("/uploads/temp/" + Paths.get(carDir).getFileName() + "/" + filename);
                img.setSortOrder(order++);
                img.setPrimary(order == 1);
                images.add(img);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        }
        if (!images.isEmpty()) {
            if (car.getImages() == null) car.setImages(new ArrayList<>());
            car.getImages().addAll(images);
        }
    }

    @ModelAttribute("modsByTag")
    public Map<Integer, List<ModsCategory>> modsByTag() {
        return tagRepository.findAll().stream()
                .collect(Collectors.toMap(ProjectTag::getId,
                        t -> modsRepository.findByProjectTag(t)));
    }
}
