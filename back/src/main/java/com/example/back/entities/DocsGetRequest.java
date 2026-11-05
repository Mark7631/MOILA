package com.example.back.entities;

public record DocsGetRequest(
    String title,
    String author,
    Integer year,
    String description,
    String type,
    String language
) {}
