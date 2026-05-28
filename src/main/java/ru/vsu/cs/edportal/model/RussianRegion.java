package ru.vsu.cs.edportal.model;

import jakarta.persistence.*;

@Entity
@Table(name = "russian_regions")
public class RussianRegion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 20)
    private String type;

    @Column(length = 100)
    private String mainCity;

    public RussianRegion() {}
    public RussianRegion(String name, String type, String mainCity) { this.name = name; this.type = type; this.mainCity = mainCity; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getMainCity() { return mainCity; }
    public void setMainCity(String mainCity) { this.mainCity = mainCity; }
}
