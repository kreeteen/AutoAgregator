package ru.vsu.cs.projectcars.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project_tags")
public class ProjectTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    public ProjectTag() {}
    public ProjectTag(String name) { this.name = name; }

    @OneToMany(mappedBy = "projectTag", cascade = CascadeType.ALL)
    private List<ModsCategory> modsCategories = new ArrayList<>();

    @OneToMany(mappedBy = "projectTag")
    private List<VehicleCar> cars = new ArrayList<>();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<ModsCategory> getModsCategories() { return modsCategories; }
    public void setModsCategories(List<ModsCategory> modsCategories) { this.modsCategories = modsCategories; }
    public List<VehicleCar> getCars() { return cars; }
    public void setCars(List<VehicleCar> cars) { this.cars = cars; }
}
