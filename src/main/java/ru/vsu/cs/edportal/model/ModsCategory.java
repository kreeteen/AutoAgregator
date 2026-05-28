package ru.vsu.cs.edportal.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mods_categories")
public class ModsCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "project_tag_id", nullable = false)
    @JsonIgnore
    private ProjectTag projectTag;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToMany(mappedBy = "selectedMods")
    @JsonIgnore
    private List<VehicleCar> cars = new ArrayList<>();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public ProjectTag getProjectTag() { return projectTag; }
    public void setProjectTag(ProjectTag projectTag) { this.projectTag = projectTag; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<VehicleCar> getCars() { return cars; }
    public void setCars(List<VehicleCar> cars) { this.cars = cars; }
}
