package ru.vsu.cs.projectcars.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.vsu.cs.projectcars.dto.ValidationResult;
import ru.vsu.cs.projectcars.model.ModsCategory;
import ru.vsu.cs.projectcars.model.ProjectTag;
import ru.vsu.cs.projectcars.model.VehicleCar;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidationServiceImplCiTest {

    private final ValidationServiceImpl validationService = new ValidationServiceImpl();
    private ProjectTag drift;
    private ProjectTag stance;
    private ProjectTag drag;

    @BeforeEach
    void setUp() {
        drift = new ProjectTag("Дрифт");
        stance = new ProjectTag("Станс");
        drag = new ProjectTag("Драг");
    }

    @Test
    void validCarShouldPassWithoutErrors() {
        ValidationResult result = validationService.validate(validCar(drift, "Выворот", "Гидроручник"));

        assertTrue(result.isValid());
        assertFalse(result.hasErrors());
    }

    @Test
    void missingRequiredFieldsShouldProduceErrors() {
        ValidationResult result = validationService.validate(new VehicleCar());

        assertTrue(result.hasErrors());
        assertTrue(result.getErrors().stream().anyMatch(error -> error.contains("год выпуска")));
        assertTrue(result.getErrors().stream().anyMatch(error -> error.contains("цену")));
        assertTrue(result.getErrors().stream().anyMatch(error -> error.contains("направление")));
    }

    @Test
    void stanceWithOffroadModificationShouldBeRejected() {
        ValidationResult result = validationService.validate(validCar(stance, "Грязевые шины MT"));

        assertTrue(result.hasErrors());
        assertTrue(result.getErrors().stream().anyMatch(error -> error.contains("Станс")));
    }

    @Test
    void driftWithoutRecommendedModificationsShouldWarn() {
        ValidationResult result = validationService.validate(validCar(drift, "Койловеры"));

        assertFalse(result.hasErrors());
        assertTrue(result.hasWarnings());
        assertEquals(2, result.getWarnings().size());
    }

    @Test
    void dragWithoutPowerModificationShouldWarn() {
        ValidationResult result = validationService.validate(validCar(drag, "Усиленная трансмиссия"));

        assertFalse(result.hasErrors());
        assertTrue(result.getWarnings().stream().anyMatch(warning -> warning.contains("турбонаддув")));
    }

    private VehicleCar validCar(ProjectTag tag, String... modificationNames) {
        VehicleCar car = new VehicleCar();
        car.setManufactureYear(2020);
        car.setPrice(new BigDecimal("1000000"));
        car.setCity("Москва");
        car.setProjectTag(tag);
        car.setEngineType("Бензин");
        car.setEngineDisplacement(2.0);
        car.setEnginePower(200);
        car.setBodyType("Седан");
        car.setSteeringSide("Левый");
        car.setMileageKm(50000);
        car.setSelectedMods(List.of(modificationNames).stream().map(this::modification).toList());
        return car;
    }

    private ModsCategory modification(String name) {
        ModsCategory modification = new ModsCategory();
        modification.setName(name);
        return modification;
    }
}
