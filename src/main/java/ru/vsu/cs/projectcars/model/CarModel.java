package ru.vsu.cs.projectcars.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "car_models")
public class CarModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private CarBrand brand;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "start_year")
    private Integer startYear;

    @Column(name = "end_year")
    private Integer endYear;

    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarGeneration> generations = new ArrayList<>();

    public CarModel() {}
    public CarModel(CarBrand brand, String name, Integer startYear, Integer endYear) {
        this.brand = brand; this.name = name; this.startYear = startYear; this.endYear = endYear;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public CarBrand getBrand() { return brand; }
    public void setBrand(CarBrand brand) { this.brand = brand; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getStartYear() { return startYear; }
    public void setStartYear(Integer startYear) { this.startYear = startYear; }
    public Integer getEndYear() { return endYear; }
    public void setEndYear(Integer endYear) { this.endYear = endYear; }
    public List<CarGeneration> getGenerations() { return generations; }
    public void setGenerations(List<CarGeneration> generations) { this.generations = generations; }
}
