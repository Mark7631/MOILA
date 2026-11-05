package com.example.back.entities;

public record ChangePasswordRequest(
    String oldPassword,
    String newPassword
) {}
