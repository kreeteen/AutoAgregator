package ru.vsu.cs.edportal.dto;

public record CarBriefResponse(
        Integer id,
        String brand,
        String model,
        Integer manufactureYear,
        String price,
        String city,
        String tagName
) {}
