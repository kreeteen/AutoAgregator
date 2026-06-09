package ru.vsu.cs.projectcars.model;

import jakarta.persistence.*;

@Entity
@Table(name = "car_generations")
public class CarGeneration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private CarModel model;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "start_year")
    private Integer startYear;

    @Column(name = "end_year")
    private Integer endYear;

    public CarGeneration() {}
    public CarGeneration(CarModel model, String name, Integer startYear, Integer endYear) {
        this.model = model; this.name = name; this.startYear = startYear; this.endYear = endYear;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public CarModel getModel() { return model; }
    public void setModel(CarModel model) { this.model = model; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getStartYear() { return startYear; }
    public void setStartYear(Integer startYear) { this.startYear = startYear; }
    public Integer getEndYear() { return endYear; }
    public void setEndYear(Integer endYear) { this.endYear = endYear; }
}
