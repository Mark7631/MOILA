package com.example.back.entities;

import java.util.List;

public record DocGetServiceResponse (
   boolean isSuccess,
   String message,
   List<DocGetResponse> docs
) {}
