package ru.vsu.cs.projectcars.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicle_cars", indexes = {
    @Index(name = "idx_car_tag", columnList = "project_tag_id"),
    @Index(name = "idx_car_user", columnList = "user_id"),
    @Index(name = "idx_car_price", columnList = "price"),
    @Index(name = "idx_car_created", columnList = "created_at")
})
public class VehicleCar {

    @Version
    private Integer version;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_tag_id", nullable = false)
    private ProjectTag projectTag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private CarBrand carBrand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id")
    private CarModel carModel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generation_id")
    private CarGeneration carGeneration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private RussianRegion region;

    @NotNull
    @Column(name = "manufacture_year", nullable = false)
    private Integer manufactureYear;

    @NotNull
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(length = 100)
    private String city;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "views_count")
    private Integer viewsCount = 0;

    @Column(name = "mileage_km")
    private Integer mileageKm;

    @Column(name = "mileage_hours")
    private Integer mileageHours;

    @Column(name = "engine_type", length = 30)
    private String engineType;

    @Column(name = "engine_displacement")
    private Double engineDisplacement;

    @Column(name = "engine_power")
    private Integer enginePower;

    @Column(name = "body_type", length = 30)
    private String bodyType;

    @Column(name = "is_damaged")
    private boolean damaged;

    @Column(name = "steering_side", length = 10)
    private String steeringSide;

    @Column(name = "is_registered")
    private boolean registered;

    @ManyToMany
    @JoinTable(
        name = "car_mods_map",
        joinColumns = @JoinColumn(name = "car_id"),
        inverseJoinColumns = @JoinColumn(name = "mod_category_id")
    )
    private List<ModsCategory> selectedMods = new ArrayList<>();

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("sortOrder ASC")
    private List<CarImage> images = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.viewsCount == null) this.viewsCount = 0;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public ProjectTag getProjectTag() { return projectTag; }
    public void setProjectTag(ProjectTag projectTag) { this.projectTag = projectTag; }
    public CarBrand getCarBrand() { return carBrand; }
    public void setCarBrand(CarBrand carBrand) { this.carBrand = carBrand; }
    public CarModel getCarModel() { return carModel; }
    public void setCarModel(CarModel carModel) { this.carModel = carModel; }
    public CarGeneration getCarGeneration() { return carGeneration; }
    public void setCarGeneration(CarGeneration carGeneration) { this.carGeneration = carGeneration; }
    public RussianRegion getRegion() { return region; }
    public void setRegion(RussianRegion region) { this.region = region; }
    public Integer getManufactureYear() { return manufactureYear; }
    public void setManufactureYear(Integer manufactureYear) { this.manufactureYear = manufactureYear; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Integer getViewsCount() { return viewsCount; }
    public void setViewsCount(Integer viewsCount) { this.viewsCount = viewsCount; }
    public Integer getMileageKm() { return mileageKm; }
    public void setMileageKm(Integer mileageKm) { this.mileageKm = mileageKm; }
    public Integer getMileageHours() { return mileageHours; }
    public void setMileageHours(Integer mileageHours) { this.mileageHours = mileageHours; }
    public String getEngineType() { return engineType; }
    public void setEngineType(String engineType) { this.engineType = engineType; }
    public Double getEngineDisplacement() { return engineDisplacement; }
    public void setEngineDisplacement(Double engineDisplacement) { this.engineDisplacement = engineDisplacement; }
    public Integer getEnginePower() { return enginePower; }
    public void setEnginePower(Integer enginePower) { this.enginePower = enginePower; }
    public String getBodyType() { return bodyType; }
    public void setBodyType(String bodyType) { this.bodyType = bodyType; }
    public boolean isDamaged() { return damaged; }
    public void setDamaged(boolean damaged) { this.damaged = damaged; }
    public String getSteeringSide() { return steeringSide; }
    public void setSteeringSide(String steeringSide) { this.steeringSide = steeringSide; }
    public boolean isRegistered() { return registered; }
    public void setRegistered(boolean registered) { this.registered = registered; }
    public List<ModsCategory> getSelectedMods() { return selectedMods; }
    public void setSelectedMods(List<ModsCategory> selectedMods) { this.selectedMods = selectedMods; }
    public List<CarImage> getImages() { return images; }
    public void setImages(List<CarImage> images) { this.images = images; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
}
