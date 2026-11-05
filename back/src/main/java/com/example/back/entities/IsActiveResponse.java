package com.example.back.entities;

public record IsActiveResponse (
    boolean isUserExist,
    boolean isActive
) {}
