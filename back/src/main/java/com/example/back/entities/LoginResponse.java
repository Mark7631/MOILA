package com.example.back.entities;

import com.example.back.enums.UserRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public record LoginResponse (
   boolean isUserExist,
   boolean isValidPassword,
   boolean isActive,
   @Enumerated(EnumType.STRING)
   UserRole userRole
) {}
