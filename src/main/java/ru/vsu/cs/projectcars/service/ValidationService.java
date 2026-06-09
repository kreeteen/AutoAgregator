package ru.vsu.cs.projectcars.service;

import ru.vsu.cs.projectcars.dto.ValidationResult;
import ru.vsu.cs.projectcars.model.VehicleCar;

public interface ValidationService {
    ValidationResult validate(VehicleCar car);
}
