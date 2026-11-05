package com.example.back.entities;

import com.example.back.enums.UserRole;

public record UserForList(
    String email,
    UserRole role,
    boolean isActive
) {}
