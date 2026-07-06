package com.lsd.rate_limiter.dto;

public record ErrorResponse(
        String message,
        int status
) {}