package com.example.back.entities;

import com.example.back.enums.UserRole;

public record DeactivateUserRequest(
    String email,
    UserRole whoDeactivate
) {}
