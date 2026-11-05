package com.example.back.entities;

public record ChangePasswordResponse(
    boolean isSuccess,
    boolean incorrectOldPassword,
    boolean isInternalError
) {}
