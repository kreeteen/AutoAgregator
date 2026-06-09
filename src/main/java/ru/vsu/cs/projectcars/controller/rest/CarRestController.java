package ru.vsu.cs.projectcars.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.projectcars.dto.CarFilter;
import ru.vsu.cs.projectcars.dto.CarResponse;
import ru.vsu.cs.projectcars.dto.ValidationResult;
import ru.vsu.cs.projectcars.model.VehicleCar;
import ru.vsu.cs.projectcars.repository.*;
import ru.vsu.cs.projectcars.security.JwtPrincipal;
import ru.vsu.cs.projectcars.service.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@Tag(name = "Автомобили", description = "REST API для управления объявлениями")
public class CarRestController {

    private final VehicleCarService carService;
    private final ValidationService validationService;
    private final UserService userService;
    private final PhoneMaskingService phoneMaskingService;

    public CarRestController(VehicleCarService carService,
                             ValidationService validationService,
                             UserService userService,
                             PhoneMaskingService phoneMaskingService) {
        this.carService = carService;
        this.validationService = validationService;
        this.userService = userService;
        this.phoneMaskingService = phoneMaskingService;
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
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Создать объявление")
    public ResponseEntity<?> create(@RequestBody VehicleCar car,
                                    @AuthenticationPrincipal JwtPrincipal principal) {
        userService.findById(principal.getUserId()).ifPresent(car::setUser);
        ValidationResult v = validationService.validate(car);
        if (v.hasErrors()) return ResponseEntity.badRequest().body(v.getErrors());
        return ResponseEntity.status(HttpStatus.CREATED).body(CarResponse.from(carService.save(car)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Обновить объявление")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody VehicleCar car) {
        ValidationResult v = validationService.validate(car);
        if (v.hasErrors()) return ResponseEntity.badRequest().body(v.getErrors());
        return ResponseEntity.ok(CarResponse.from(carService.update(id, car)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Удалить объявление")
    public ResponseEntity<Void> delete(@PathVariable Integer id,
                                       @AuthenticationPrincipal JwtPrincipal principal) {
        var car = carService.findByIdWithAll(id);
        if (car.isEmpty())
            return ResponseEntity.notFound().build();
        if (!car.get().getUser().getId().equals(principal.getUserId()))
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
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Номер телефона продавца (только для авторизованных)")
    public ResponseEntity<String> getSellerPhone(@PathVariable Integer id) {
        return carService.findById(id)
                .map(car -> ResponseEntity.ok(userService.getPhoneNumber(car.getUser().getId())))
                .orElse(ResponseEntity.notFound().build());
    }
}
