package com.example.back.entities;

import java.io.InputStream;

public record DownloadResponse (
   boolean isSuccess,
   String message,
   String fileName,
   InputStream doc
) {}
