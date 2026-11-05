package com.example.back.entities;

import com.example.back.enums.UserRole;

public record SignUpRequest(
   String email,
   String password,
   UserRole role
) {}
