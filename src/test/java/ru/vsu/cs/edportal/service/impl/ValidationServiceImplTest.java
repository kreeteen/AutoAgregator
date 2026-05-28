package ru.vsu.cs.edportal.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.vsu.cs.edportal.dto.ValidationResult;
import ru.vsu.cs.edportal.model.ModsCategory;
import ru.vsu.cs.edportal.model.ProjectTag;
import ru.vsu.cs.edportal.model.VehicleCar;
import ru.vsu.cs.edportal.service.ValidationService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidationServiceImplTest {

    private final ValidationService validationService = new ValidationServiceImpl();

    private ProjectTag drift;
    private ProjectTag stance;
    private ProjectTag drag;

    @BeforeEach
    void setUp() {
        drift = new ProjectTag(); drift.setId(1); drift.setName("Дрифт");
        stance = new ProjectTag(); stance.setId(2); stance.setName("Станс");
        drag = new ProjectTag(); drag.setId(3); drag.setName("Драг");
    }

    private VehicleCar validBase() {
        VehicleCar car = new VehicleCar();
        car.setEngineType("Бензин");
        car.setEngineDisplacement(2.0);
        car.setEnginePower(200);
        car.setBodyType("Седан");
        car.setSteeringSide("Левый");
        car.setMileageKm(50000);
        return car;
    }

    @Test
    @DisplayName("Stance + off-road mod should reject")
    void stanceWithOffroad_shouldReject() {
        VehicleCar car = validBase();
        car.setProjectTag(stance);
        ModsCategory mud = new ModsCategory(); mud.setName("Грязевые шины MT");
        car.setSelectedMods(List.of(mud));

        ValidationResult r = validationService.validate(car);
        assertTrue(r.hasErrors());
        assertTrue(r.getErrors().stream().anyMatch(e -> e.contains("Станс")));
    }

    @Test
    @DisplayName("Stance without off-road should pass")
    void stanceWithoutOffroad_shouldPass() {
        VehicleCar car = validBase();
        car.setProjectTag(stance);
        ModsCategory air = new ModsCategory(); air.setName("Пневмоподвеска");
        car.setSelectedMods(List.of(air));

        assertTrue(validationService.validate(car).isValid());
    }

    @Test
    @DisplayName("Drift without angle kit should warn")
    void driftWithoutAngle_shouldWarn() {
        VehicleCar car = validBase();
        car.setProjectTag(drift);
        ModsCategory hb = new ModsCategory(); hb.setName("Гидроручник");
        car.setSelectedMods(List.of(hb));

        ValidationResult r = validationService.validate(car);
        assertFalse(r.hasErrors());
        assertTrue(r.hasWarnings());
        assertTrue(r.getWarnings().stream().anyMatch(w -> w.contains("выворот")));
    }

    @Test
    @DisplayName("Drift with angle kit should not warn about angle")
    void driftWithAngle_shouldNotWarn() {
        VehicleCar car = validBase();
        car.setProjectTag(drift);
        ModsCategory angle = new ModsCategory(); angle.setName("Выворот 55°");
        ModsCategory hb = new ModsCategory(); hb.setName("Гидроручник");
        car.setSelectedMods(List.of(angle, hb));

        ValidationResult r = validationService.validate(car);
        assertTrue(r.isValid());
    }

    @Test
    @DisplayName("Drag without turbo should warn")
    void dragWithoutTurbo_shouldWarn() {
        VehicleCar car = validBase();
        car.setProjectTag(drag);
        ModsCategory trans = new ModsCategory(); trans.setName("Усиленная трансмиссия");
        car.setSelectedMods(List.of(trans));

        ValidationResult r = validationService.validate(car);
        assertFalse(r.hasErrors());
        assertTrue(r.hasWarnings());
    }

    @Test
    @DisplayName("No tag should produce error")
    void noTag_shouldError() {
        VehicleCar car = new VehicleCar();
        ValidationResult r = validationService.validate(car);
        assertTrue(r.hasErrors());
    }
}
