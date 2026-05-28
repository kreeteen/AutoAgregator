package ru.vsu.cs.edportal.service;

import ru.vsu.cs.edportal.dto.ValidationResult;
import ru.vsu.cs.edportal.model.VehicleCar;

public interface ValidationService {
    ValidationResult validate(VehicleCar car);
}
