package ru.vsu.cs.projectcars.dto;

import ru.vsu.cs.projectcars.model.VehicleCar;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CarResponse(
        Integer id,
        String brand,
        String model,
        Integer manufactureYear,
        BigDecimal price,
        String city,
        String description,
        String tagName,
        Integer tagId,
        List<String> mods,
        String sellerName,
        Integer sellerId,
        LocalDateTime createdAt
) {
    public static CarResponse from(VehicleCar car) {
        String seller = car.getUser() != null
                ? car.getUser().getFirstName()
                    + (car.getUser().getLastName() != null ? " " + car.getUser().getLastName() : "")
                : "Неизвестный";
        return new CarResponse(
                car.getId(),
                car.getCarBrand() != null ? car.getCarBrand().getName() : "",
                car.getCarModel() != null ? car.getCarModel().getName() : "",
                car.getManufactureYear(), car.getPrice(), car.getCity(),
                car.getDescription(),
                car.getProjectTag().getName(), car.getProjectTag().getId(),
                car.getSelectedMods().stream().map(m -> m.getName()).toList(),
                seller, car.getUser().getId(), car.getCreatedAt()
        );
    }
}
