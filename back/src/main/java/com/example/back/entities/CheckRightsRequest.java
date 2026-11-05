package com.example.back.entities;

import com.example.back.enums.UserRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public record CheckRightsRequest(
    String email,
    @Enumerated(EnumType.STRING)
    UserRole role
) {}
