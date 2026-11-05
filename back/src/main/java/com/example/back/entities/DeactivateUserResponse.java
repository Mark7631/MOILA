package com.example.back.entities;

public record DeactivateUserResponse (
        boolean isSuccess,
        boolean isForbidden,
        boolean noSuchUser
) {}
