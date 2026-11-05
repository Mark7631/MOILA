package com.example.back.entities;

import org.springframework.web.multipart.MultipartFile;

public record DocPutRequest(
    String title,
    String author,
    Integer year,
    String description,
    String type,
    String language,
    MultipartFile file
) {}
