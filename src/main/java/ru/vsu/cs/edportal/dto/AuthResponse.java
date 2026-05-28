package ru.vsu.cs.edportal.dto;

public record AuthResponse(
        String token,
        UserSession user
) {}
