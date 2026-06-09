package ru.vsu.cs.projectcars.dto;

import java.math.BigDecimal;
import java.util.List;

public class CarFilter {

    private Integer projectTagId;
    private String brand;
    private String model;
    private String query;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String city;
    private Integer regionId;
    private Integer minYear;
    private Integer maxYear;
    private Integer modCategoryId;
    private List<Integer> modCategoryIds;
    private Integer brandId;
    private Integer modelId;
    private Integer generationId;
    private Integer minMileageKm;
    private Integer maxMileageKm;
    private Integer minMileageHours;
    private Integer maxMileageHours;
    private String engineType;
    private Double minEngineDisplacement;
    private Double maxEngineDisplacement;
    private Integer minEnginePower;
    private Integer maxEnginePower;
    private String bodyType;
    private Boolean damaged;
    private String steeringSide;
    private Boolean registered;

    public Integer getProjectTagId() { return projectTagId; }
    public void setProjectTagId(Integer projectTagId) { this.projectTagId = projectTagId; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }
    public BigDecimal getMaxPrice() { return maxPrice; }
    public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public Integer getRegionId() { return regionId; }
    public void setRegionId(Integer regionId) { this.regionId = regionId; }
    public Integer getMinYear() { return minYear; }
    public void setMinYear(Integer minYear) { this.minYear = minYear; }
    public Integer getMaxYear() { return maxYear; }
    public void setMaxYear(Integer maxYear) { this.maxYear = maxYear; }
    public Integer getModCategoryId() { return modCategoryId; }
    public void setModCategoryId(Integer modCategoryId) { this.modCategoryId = modCategoryId; }
    public List<Integer> getModCategoryIds() { return modCategoryIds; }
    public void setModCategoryIds(List<Integer> modCategoryIds) { this.modCategoryIds = modCategoryIds; }
    public Integer getBrandId() { return brandId; }
    public void setBrandId(Integer brandId) { this.brandId = brandId; }
    public Integer getModelId() { return modelId; }
    public void setModelId(Integer modelId) { this.modelId = modelId; }
    public Integer getGenerationId() { return generationId; }
    public void setGenerationId(Integer generationId) { this.generationId = generationId; }
    public Integer getMinMileageKm() { return minMileageKm; }
    public void setMinMileageKm(Integer minMileageKm) { this.minMileageKm = minMileageKm; }
    public Integer getMaxMileageKm() { return maxMileageKm; }
    public void setMaxMileageKm(Integer maxMileageKm) { this.maxMileageKm = maxMileageKm; }
    public Integer getMinMileageHours() { return minMileageHours; }
    public void setMinMileageHours(Integer minMileageHours) { this.minMileageHours = minMileageHours; }
    public Integer getMaxMileageHours() { return maxMileageHours; }
    public void setMaxMileageHours(Integer maxMileageHours) { this.maxMileageHours = maxMileageHours; }
    public String getEngineType() { return engineType; }
    public void setEngineType(String engineType) { this.engineType = engineType; }
    public Double getMinEngineDisplacement() { return minEngineDisplacement; }
    public void setMinEngineDisplacement(Double minEngineDisplacement) { this.minEngineDisplacement = minEngineDisplacement; }
    public Double getMaxEngineDisplacement() { return maxEngineDisplacement; }
    public void setMaxEngineDisplacement(Double maxEngineDisplacement) { this.maxEngineDisplacement = maxEngineDisplacement; }
    public Integer getMinEnginePower() { return minEnginePower; }
    public void setMinEnginePower(Integer minEnginePower) { this.minEnginePower = minEnginePower; }
    public Integer getMaxEnginePower() { return maxEnginePower; }
    public void setMaxEnginePower(Integer maxEnginePower) { this.maxEnginePower = maxEnginePower; }
    public String getBodyType() { return bodyType; }
    public void setBodyType(String bodyType) { this.bodyType = bodyType; }
    public Boolean getDamaged() { return damaged; }
    public void setDamaged(Boolean damaged) { this.damaged = damaged; }
    public String getSteeringSide() { return steeringSide; }
    public void setSteeringSide(String steeringSide) { this.steeringSide = steeringSide; }
    public Boolean getRegistered() { return registered; }
    public void setRegistered(Boolean registered) { this.registered = registered; }
}
