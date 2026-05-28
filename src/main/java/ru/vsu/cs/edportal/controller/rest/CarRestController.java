package ru.vsu.cs.edportal.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.edportal.dto.CarFilter;
import ru.vsu.cs.edportal.dto.CarResponse;
import ru.vsu.cs.edportal.dto.ValidationResult;
import ru.vsu.cs.edportal.model.VehicleCar;
import ru.vsu.cs.edportal.repository.*;
import ru.vsu.cs.edportal.security.UserContext;
import ru.vsu.cs.edportal.service.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@Tag(name = "Автомобили", description = "REST API для управления объявлениями")
public class CarRestController {

    private final VehicleCarService carService;
    private final ModsCategoryRepository modsRepository;
    private final ValidationService validationService;
    private final UserService userService;
    private final PhoneMaskingService phoneMaskingService;
    private final UserContext userContext;

    public CarRestController(VehicleCarService carService,
                             ModsCategoryRepository modsRepository,
                             ValidationService validationService,
                             UserService userService,
                             PhoneMaskingService phoneMaskingService,
                             UserContext userContext) {
        this.carService = carService;
        this.modsRepository = modsRepository;
        this.validationService = validationService;
        this.userService = userService;
        this.phoneMaskingService = phoneMaskingService;
        this.userContext = userContext;
    }

    @GetMapping
    @Operation(summary = "Список автомобилей с фильтрацией и пагинацией")
    public Page<CarResponse> getAll(CarFilter filter, Pageable pageable) {
        return carService.findByFilterPaged(filter, pageable)
                .map(CarResponse::from);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Автомобиль по ID")
    public ResponseEntity<CarResponse> getById(@PathVariable Integer id) {
        return carService.findById(id)
                .map(c -> ResponseEntity.ok(CarResponse.from(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Создать объявление")
    public ResponseEntity<?> create(@RequestBody VehicleCar car) {
        if (!userContext.isAuthenticated())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        userService.findById(userContext.getUserId()).ifPresent(car::setUser);
        ValidationResult v = validationService.validate(car);
        if (v.hasErrors()) return ResponseEntity.badRequest().body(v.getErrors());
        return ResponseEntity.status(HttpStatus.CREATED).body(CarResponse.from(carService.save(car)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить объявление")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody VehicleCar car) {
        ValidationResult v = validationService.validate(car);
        if (v.hasErrors()) return ResponseEntity.badRequest().body(v.getErrors());
        return ResponseEntity.ok(CarResponse.from(carService.update(id, car)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить объявление")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!userContext.isAuthenticated())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        var car = carService.findByIdWithAll(id);
        if (car.isEmpty())
            return ResponseEntity.notFound().build();
        if (!car.get().getUser().getId().equals(userContext.getUserId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        carService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск по модификации")
    public List<CarResponse> search(@RequestParam("q") String query) {
        return carService.searchByMod(query).stream()
                .map(CarResponse::from).toList();
    }

    @GetMapping("/{id}/phone")
    @Operation(summary = "Номер телефона продавца (только для авторизованных)")
    public ResponseEntity<String> getSellerPhone(@PathVariable Integer id) {
        if (!userContext.isAuthenticated())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return carService.findById(id)
                .map(car -> ResponseEntity.ok(userService.getPhoneNumber(car.getUser().getId())))
                .orElse(ResponseEntity.notFound().build());
    }
}
