package ru.vsu.cs.projectcars.dto;

public record CarBriefResponse(
        Integer id,
        String brand,
        String model,
        Integer manufactureYear,
        String price,
        String city,
        String tagName
) {}
