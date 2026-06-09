package ru.vsu.cs.projectcars.dto;

public record AuthResponse(
        String token,
        UserSession user
) {}
