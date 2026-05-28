package ru.vsu.cs.edportal.service.impl;

import org.springframework.stereotype.Service;
import ru.vsu.cs.edportal.dto.ValidationResult;
import ru.vsu.cs.edportal.model.VehicleCar;
import ru.vsu.cs.edportal.service.ValidationService;

@Service
public class ValidationServiceImpl implements ValidationService {

    @Override
    public ValidationResult validate(VehicleCar car) {
        ValidationResult result = new ValidationResult();

        if (car.getProjectTag() == null) {
            result.addError("Не выбрано направление тюнинга");
            return result;
        }

        if (car.getEngineType() == null || car.getEngineType().isBlank()) {
            result.addError("Укажите тип двигателя");
        }
        if (car.getEngineDisplacement() == null || car.getEngineDisplacement() <= 0) {
            result.addError("Укажите объём двигателя");
        }
        if (car.getEnginePower() == null || car.getEnginePower() <= 0) {
            result.addError("Укажите мощность двигателя");
        }
        if (car.getBodyType() == null || car.getBodyType().isBlank()) {
            result.addError("Укажите тип кузова");
        }
        if (car.getSteeringSide() == null || car.getSteeringSide().isBlank()) {
            result.addError("Укажите расположение руля");
        }
        if (car.getMileageKm() == null && car.getMileageHours() == null) {
            result.addError("Укажите пробег (км) или моточасы");
        }

        String tagName = car.getProjectTag().getName().toLowerCase();

        validateStanceNoOffroad(tagName, car, result);
        validateDriftHasAngleKit(tagName, car, result);
        validateDriftHasHandbrake(tagName, car, result);
        validateDragHasPower(tagName, car, result);

        return result;
    }

    private void validateStanceNoOffroad(String tagName, VehicleCar car, ValidationResult result) {
        if (tagName.contains("станс")) {
            boolean hasOffroadMod = car.getSelectedMods().stream()
                    .anyMatch(m -> m.getName().toLowerCase().contains("грязев")
                            || m.getName().toLowerCase().contains("off-road")
                            || m.getName().toLowerCase().contains("внедорож")
                            || m.getName().toLowerCase().contains("mt"));
            if (hasOffroadMod) {
                result.addError("Направление 'Станс' несовместимо с внедорожными модификациями");
            }
        }
    }

    private void validateDriftHasAngleKit(String tagName, VehicleCar car, ValidationResult result) {
        if (tagName.contains("дрифт")) {
            boolean hasAngleKit = car.getSelectedMods().stream()
                    .anyMatch(m -> m.getName().toLowerCase().contains("выворот")
                            || m.getName().toLowerCase().contains("angle"));
            if (!hasAngleKit) {
                result.addWarning("Для дрифт-проекта рекомендуется установка kits угла поворота (выворот)");
            }
        }
    }

    private void validateDriftHasHandbrake(String tagName, VehicleCar car, ValidationResult result) {
        if (tagName.contains("дрифт")) {
            boolean hasHandbrake = car.getSelectedMods().stream()
                    .anyMatch(m -> m.getName().toLowerCase().contains("гидроручник")
                            || m.getName().toLowerCase().contains("handbrake"));
            if (!hasHandbrake) {
                result.addWarning("Для дрифт-проекта рекомендуется гидроручник");
            }
        }
    }

    private void validateDragHasPower(String tagName, VehicleCar car, ValidationResult result) {
        if (tagName.contains("драг")) {
            boolean hasBoost = car.getSelectedMods().stream()
                    .anyMatch(m -> m.getName().toLowerCase().contains("турбо")
                            || m.getName().toLowerCase().contains("нитро")
                            || m.getName().toLowerCase().contains("наддув"));
            if (!hasBoost) {
                result.addWarning("Для драг-проекта рекомендуется турбонаддув или система закиси азота");
            }
        }
    }
}
