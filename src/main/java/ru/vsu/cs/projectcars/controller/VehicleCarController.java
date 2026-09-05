package ru.vsu.cs.projectcars.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.vsu.cs.projectcars.dto.CarFilter;
import ru.vsu.cs.projectcars.dto.ValidationResult;
import ru.vsu.cs.projectcars.model.*;
import ru.vsu.cs.projectcars.repository.*;
import ru.vsu.cs.projectcars.security.JwtPrincipal;
import ru.vsu.cs.projectcars.service.FileStorageService;
import ru.vsu.cs.projectcars.service.PhoneMaskingService;
import ru.vsu.cs.projectcars.service.UserService;
import ru.vsu.cs.projectcars.service.VehicleCarService;
import ru.vsu.cs.projectcars.service.ValidationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cars")
public class VehicleCarController {

    private static final Logger log = LoggerFactory.getLogger(VehicleCarController.class);

    private final VehicleCarService carService;
    private final ProjectTagRepository tagRepository;
    private final ModsCategoryRepository modsRepository;
    private final ValidationService validationService;
    private final PhoneMaskingService phoneMaskingService;
    private final UserService userService;
    private final CarBrandRepository brandRepository;
    private final CarModelRepository modelRepository;
    private final CarGenerationRepository generationRepository;
    private final RussianRegionRepository regionRepository;
    private final FileStorageService fileStorageService;

    @InitBinder("car")
    public void initCarBinder(WebDataBinder binder) {
        binder.setDisallowedFields("images");
    }

    public VehicleCarController(VehicleCarService carService,
                                ProjectTagRepository tagRepository,
                                ModsCategoryRepository modsRepository,
                                ValidationService validationService,
                                PhoneMaskingService phoneMaskingService,
                                UserService userService,
                                CarBrandRepository brandRepository,
                                CarModelRepository modelRepository,
                                CarGenerationRepository generationRepository,
                                RussianRegionRepository regionRepository,
                                FileStorageService fileStorageService) {
        this.carService = carService;
        this.tagRepository = tagRepository;
        this.modsRepository = modsRepository;
        this.validationService = validationService;
        this.phoneMaskingService = phoneMaskingService;
        this.userService = userService;
        this.brandRepository = brandRepository;
        this.modelRepository = modelRepository;
        this.generationRepository = generationRepository;
        this.regionRepository = regionRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String list(CarFilter filter,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       HttpSession session,
                       Model model) {
        if (filter.getRegionId() == null) {
            Object attr = session.getAttribute("selectedRegionId");
            if (attr instanceof Integer sid) filter.setRegionId(sid);
        }
        Page<VehicleCar> carPage = carService.findByFilterPagedWithInit(filter, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
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
                        HttpSession session,
                        Model model) {
        if (filter.getRegionId() == null) {
            Object attr = session.getAttribute("selectedRegionId");
            if (attr instanceof Integer sid) filter.setRegionId(sid);
        }
        filter.setProjectTagId(tagId);
        Page<VehicleCar> carPage = carService.findByFilterPagedWithInit(filter, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
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

    @GetMapping("/api/page")
    public String nextPage(CarFilter filter,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "20") int size,
                           HttpSession session,
                           Model model) {
        applySelectedRegion(filter, session);
        Page<VehicleCar> carPage = carService.findByFilterPagedWithInit(filter,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        model.addAttribute("cars", carPage.getContent());
        return "cars/card-fragment :: carCards";
    }

    @GetMapping("/api/tag/{tagId}/page")
    public String nextTagPage(@PathVariable Integer tagId,
                              CarFilter filter,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "20") int size,
                              HttpSession session,
                              Model model) {
        applySelectedRegion(filter, session);
        filter.setProjectTagId(tagId);
        Page<VehicleCar> carPage = carService.findByFilterPagedWithInit(filter,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        model.addAttribute("cars", carPage.getContent());
        return "cars/card-fragment :: carCards";
    }

    private void applySelectedRegion(CarFilter filter, HttpSession session) {
        if (filter.getRegionId() == null) {
            Object attr = session.getAttribute("selectedRegionId");
            if (attr instanceof Integer sid) filter.setRegionId(sid);
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model,
                         @AuthenticationPrincipal JwtPrincipal principal,
                         HttpServletRequest request) {
        Optional<VehicleCar> opt = carService.findByIdWithAll(id);
        if (opt.isEmpty()) return "redirect:/cars";
        VehicleCar car = opt.get();
        carService.incrementView(id, request.getRemoteAddr());
        model.addAttribute("car", car);
        String brand = car.getCarBrand() != null ? car.getCarBrand().getName() : "";
        String modelName = car.getCarModel() != null ? car.getCarModel().getName() : "";
        model.addAttribute("pageTitle", (brand + " " + modelName).trim() + " — ProjectCars Marketplace");

        List<ModsCategory> mods = car.getSelectedMods();
        if (mods != null && !mods.isEmpty()) {
            model.addAttribute("carModsHtml", mods.stream()
                    .map(m -> "<span class=\"badge bg-info text-dark\">" + m.getName() + "</span>")
                    .collect(Collectors.joining(" ")));
        } else {
            model.addAttribute("carModsHtml", "");
        }

        if (principal != null) {
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
    public String create(@AuthenticationPrincipal JwtPrincipal principal,
                         @ModelAttribute("car") VehicleCar car, BindingResult result,
                         @RequestParam(value = "modIds", required = false) List<Integer> modIds,
                         @RequestParam(value = "images", required = false) List<MultipartFile> imageFiles,
                         @RequestParam(value = "brandId", required = false) Integer brandId,
                         @RequestParam(value = "modelId", required = false) Integer modelId,
                         @RequestParam(value = "genId", required = false) Integer genId,
                         @RequestParam(value = "regionId", required = false) Integer regionId,
                         @RequestParam(value = "tagId", required = false) Integer tagId,
                         Model model) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("tags", tagRepository.findAll());
                model.addAttribute("brands", brandRepository.findAllByOrderByNameAsc());
                model.addAttribute("regions", regionRepository.findAllByOrderByNameAsc());
                return "cars/form";
            }
            if (principal == null) return "redirect:/login";

            log.info(">>> User ID: {}", principal.getUserId());

            userService.findById(principal.getUserId()).ifPresent(car::setUser);
            resolveEntityRefs(car, brandId, modelId, genId, regionId, tagId, modIds);

            log.info(">>> Saving car: price={}, city={}, year={}, engineType={}, modsCount={}",
                    car.getPrice(), car.getCity(), car.getManufactureYear(), car.getEngineType(),
                    car.getSelectedMods() != null ? car.getSelectedMods().size() : 0);

            VehicleCar saved = carService.save(car);
            log.info(">>> Saved car id={}", saved.getId());

            List<CarImage> images = saveUploadedImages(saved.getId(), imageFiles);
            if (!images.isEmpty()) {
                images.forEach(img -> img.setCar(saved));
                saved.setImages(images);
                carService.save(saved);
            }

            ValidationResult v = validationService.validate(saved);
            if (v.hasErrors()) {
                fileStorageService.deleteImages(saved.getImages());
                carService.delete(saved.getId());
                model.addAttribute("tags", tagRepository.findAll());
                model.addAttribute("brands", brandRepository.findAllByOrderByNameAsc());
                model.addAttribute("regions", regionRepository.findAllByOrderByNameAsc());
                model.addAttribute("validationErrors", v.getErrors());
                model.addAttribute("validationWarnings", v.getWarnings());
                return "cars/form";
            }
            log.info(">>> Create success, redirecting to /cars/{}", saved.getId());
            return "redirect:/cars/" + saved.getId();
        } catch (Exception e) {
            log.error("Error creating car: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, @AuthenticationPrincipal JwtPrincipal principal,
                           @RequestParam(value = "redirect", required = false) String redirect,
                           HttpServletRequest request,
                           Model model) {
        if (principal == null) return "redirect:/login";
        Optional<VehicleCar> opt = carService.findByIdWithAll(id);
        if (opt.isEmpty()) return "redirect:/cars";
        VehicleCar car = opt.get();
        if (!car.getUser().getId().equals(principal.getUserId())) {
            return "redirect:/cars";
        }
        if (redirect == null || redirect.isBlank()) {
            String referer = request.getHeader("Referer");
            if (referer != null && !referer.contains("/edit") && !referer.contains("/new")) {
                redirect = referer;
            }
        }
        model.addAttribute("car", car);
        model.addAttribute("redirect", redirect);
        model.addAttribute("tags", tagRepository.findAll());
        model.addAttribute("brands", brandRepository.findAllByOrderByNameAsc());
        model.addAttribute("regions", regionRepository.findAllByOrderByNameAsc());
        if (car.getCarBrand() != null) {
            model.addAttribute("models", modelRepository.findByBrandIdOrderByNameAsc(car.getCarBrand().getId()));
        }
        if (car.getCarModel() != null) {
            model.addAttribute("generations", generationRepository.findByModelIdOrderByNameAsc(car.getCarModel().getId()));
        }
        return "cars/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id, @AuthenticationPrincipal JwtPrincipal principal,
                         @ModelAttribute("car") VehicleCar car, BindingResult result,
                         @RequestParam(value = "modIds", required = false) List<Integer> modIds,
                         @RequestParam(value = "images", required = false) List<MultipartFile> imageFiles,
                         @RequestParam(value = "brandId", required = false) Integer brandId,
                         @RequestParam(value = "modelId", required = false) Integer modelId,
                         @RequestParam(value = "genId", required = false) Integer genId,
                         @RequestParam(value = "regionId", required = false) Integer regionId,
                          @RequestParam(value = "tagId", required = false) Integer tagId,
                          @RequestParam(value = "redirect", required = false) String redirect,
                          Model model) {
        try {
            if (principal == null) return "redirect:/login";
            resolveEntityRefs(car, brandId, modelId, genId, regionId, tagId, modIds);
            if (result.hasErrors()) {
                result.getAllErrors().forEach(err -> log.warn("  field='{}', msg='{}'",
                        err instanceof org.springframework.validation.FieldError fe ? fe.getField() : "?",
                        err.getDefaultMessage()));
                model.addAttribute("tags", tagRepository.findAll());
                model.addAttribute("brands", brandRepository.findAllByOrderByNameAsc());
                model.addAttribute("regions", regionRepository.findAllByOrderByNameAsc());
                model.addAttribute("redirect", redirect);
                return "cars/form";
            }
            Optional<VehicleCar> existing = carService.findByIdWithAll(id);
            if (existing.isEmpty()) return "redirect:/cars";
            if (!existing.get().getUser().getId().equals(principal.getUserId())) {
                return "redirect:/cars";
            }

            if (modIds == null) {
                car.setSelectedMods(new ArrayList<>(existing.get().getSelectedMods()));
            }

            List<CarImage> newImages = saveUploadedImages(id, imageFiles);
            car.setImages(newImages);

            log.info(">>> Before validation: car price={}, city={}, year={}, engineType={}, modsCount={}",
                    car.getPrice(), car.getCity(), car.getManufactureYear(), car.getEngineType(),
                    car.getSelectedMods() != null ? car.getSelectedMods().size() : 0);
            ValidationResult v = validationService.validate(car);
            log.info("    validation errors={}, warnings={}", v.getErrors(), v.getWarnings());
            if (v.hasErrors()) {
                fileStorageService.deleteImages(newImages);
                model.addAttribute("tags", tagRepository.findAll());
                model.addAttribute("brands", brandRepository.findAllByOrderByNameAsc());
                model.addAttribute("regions", regionRepository.findAllByOrderByNameAsc());
                model.addAttribute("validationErrors", v.getErrors());
                model.addAttribute("validationWarnings", v.getWarnings());
                model.addAttribute("redirect", redirect);
                return "cars/form";
            }
            log.info(">>> Calling carService.update({})...", id);
            carService.update(id, car);
            log.info("<<< Update completed");
            if (redirect != null && !redirect.isBlank()) {
                return "redirect:" + redirect;
            }
            return "redirect:/cars/" + id;
        } catch (Exception e) {
            log.error("Error updating car {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id,
                         @AuthenticationPrincipal JwtPrincipal principal) {
        if (principal == null) return "redirect:/login";
        Optional<VehicleCar> car = carService.findByIdWithAll(id);
        if (car.isEmpty()) return "redirect:/cars";
        if (!car.get().getUser().getId().equals(principal.getUserId())) return "redirect:/cars";
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

    private List<CarImage> saveUploadedImages(Integer carId, List<MultipartFile> imageFiles) {
        if (imageFiles == null || imageFiles.isEmpty()) return List.of();
        List<CarImage> images = new ArrayList<>();
        int order = 0;
        for (MultipartFile file : imageFiles) {
            if (file.isEmpty()) continue;
            images.add(fileStorageService.saveImage(carId, file, order++));
        }
        return images;
    }

    @ModelAttribute("modsByTag")
    public Map<Integer, List<ModsCategory>> modsByTag() {
        return tagRepository.findAll().stream()
                .collect(Collectors.toMap(ProjectTag::getId,
                        t -> modsRepository.findByProjectTag(t)));
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, jakarta.servlet.http.HttpServletRequest request,
                                   jakarta.servlet.http.HttpServletResponse response, Model model) {
        log.error("Unhandled exception processing {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
        model.addAttribute("errorMessage", "Произошла внутренняя ошибка: " + ex.getMessage());
        return "error";
    }
}
