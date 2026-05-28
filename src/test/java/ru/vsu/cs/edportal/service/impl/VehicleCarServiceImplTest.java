package ru.vsu.cs.edportal.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.vsu.cs.edportal.model.ModsCategory;
import ru.vsu.cs.edportal.model.ProjectTag;
import ru.vsu.cs.edportal.model.User;
import ru.vsu.cs.edportal.model.VehicleCar;
import ru.vsu.cs.edportal.repository.VehicleCarRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleCarServiceImplTest {

    @Mock private VehicleCarRepository repository;
    @InjectMocks private VehicleCarServiceImpl service;

    @Test
    @DisplayName("findById should return car when exists")
    void findById_exists() {
        VehicleCar car = new VehicleCar();
        car.setId(1);
        when(repository.findById(1)).thenReturn(Optional.of(car));
        assertTrue(service.findById(1).isPresent());
    }

    @Test
    @DisplayName("findById should return empty when not exists")
    void findById_notExists() {
        when(repository.findById(99)).thenReturn(Optional.empty());
        assertTrue(service.findById(99).isEmpty());
    }

    @Test
    @DisplayName("save should delegate to repository")
    void save() {
        VehicleCar car = new VehicleCar();
        car.setManufactureYear(1997);
        car.setPrice(new BigDecimal("3500000"));
        car.setCity("Moscow");
        User user = new User();
        user.setId(1);
        car.setUser(user);
        ProjectTag tag = new ProjectTag();
        tag.setId(1);
        car.setProjectTag(tag);

        when(repository.save(any())).thenAnswer(i -> { VehicleCar c = i.getArgument(0); c.setId(1); return c; });

        VehicleCar saved = service.save(car);
        assertNotNull(saved);
    }

    @Test
    @DisplayName("delete should call repository deleteById")
    void delete() {
        service.delete(1);
        verify(repository).deleteById(1);
    }

    @Test
    @DisplayName("searchByMod should find cars with matching mod")
    void searchByMod() {
        VehicleCar car = new VehicleCar();
        car.setId(1);
        ModsCategory mod = new ModsCategory();
        mod.setName("Гидроручник");
        car.setSelectedMods(List.of(mod));

        when(repository.findAllWithAssociations()).thenReturn(List.of(car));

        List<VehicleCar> results = service.searchByMod("Гидроручник");
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("searchByMod with no match should return empty")
    void searchByMod_noMatch() {
        VehicleCar car = new VehicleCar();
        car.setId(1);
        ModsCategory mod = new ModsCategory();
        mod.setName("Пневма");
        car.setSelectedMods(List.of(mod));

        when(repository.findAllWithAssociations()).thenReturn(List.of(car));

        List<VehicleCar> results = service.searchByMod("Турбина");
        assertTrue(results.isEmpty());
    }
}
