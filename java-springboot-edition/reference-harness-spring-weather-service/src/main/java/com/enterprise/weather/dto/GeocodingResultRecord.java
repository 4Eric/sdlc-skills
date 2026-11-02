package com.enterprise.weather.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.annotation.Nullable;

/**
 * Phase 2 Locked Data Contract (Single Source of Truth).
 * Shared across Client, Service, Controller, and Test subagents to prevent interface drift.
 */
public record GeocodingResultRecord(
    long id,
    @NotBlank String name,
    @DecimalMin("-90.0") @DecimalMax("90.0") double latitude,
    @DecimalMin("-180.0") @DecimalMax("180.0") double longitude,
    @NotBlank String country,
    @Nullable String admin1
) {
    public String formattedDisplayName() {
        return (admin1 != null && !admin1.isBlank())
            ? "%s, %s, %s".formatted(name, admin1, country)
            : "%s, %s".formatted(name, country);
    }
}
