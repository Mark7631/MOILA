package com.example.back.entities;

public record DocGetResponse (
        Long id,
        String title,
        String author,
        Integer year,
        String description,
        String type,
        String language
) {}
