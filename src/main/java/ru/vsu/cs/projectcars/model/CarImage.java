package ru.vsu.cs.projectcars.model;

import jakarta.persistence.*;

@Entity
@Table(name = "car_images")
public class CarImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private VehicleCar car;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "is_primary")
    private boolean primary;

    public CarImage() {}
    public CarImage(VehicleCar car, String filePath, Integer sortOrder, boolean primary) {
        this.car = car; this.filePath = filePath; this.sortOrder = sortOrder; this.primary = primary;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public VehicleCar getCar() { return car; }
    public void setCar(VehicleCar car) { this.car = car; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public boolean isPrimary() { return primary; }
    public void setPrimary(boolean primary) { this.primary = primary; }
}
