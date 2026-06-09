package ru.vsu.cs.projectcars.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "car_brands")
public class CarBrand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 100)
    private String country;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarModel> models = new ArrayList<>();

    public CarBrand() {}
    public CarBrand(String name, String country) { this.name = name; this.country = country; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public List<CarModel> getModels() { return models; }
    public void setModels(List<CarModel> models) { this.models = models; }
}
