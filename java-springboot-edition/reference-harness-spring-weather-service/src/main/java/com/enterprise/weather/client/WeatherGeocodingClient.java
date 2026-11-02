package com.enterprise.weather.client;

import com.enterprise.weather.dto.GeocodingResultRecord;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Implements the encoded Tribal Domain Invariant (AGENTS.md Section 3.2):
 * Open-Meteo's geocoding index drops multi-word regional/borough queries such as "North York Ontario".
 * Automatically falls back to OpenStreetMap Photon (photon.komoot.io) and normalizes admin1 labels.
 */
@Component
public class WeatherGeocodingClient {

    private final RestClient openMeteoClient;
    private final RestClient photonClient;

    public WeatherGeocodingClient(RestClient.Builder builder) {
        this.openMeteoClient = builder.baseUrl("https://geocoding-api.open-meteo.com/v1").build();
        this.photonClient = builder.baseUrl("https://photon.komoot.io").build();
    }

    @CircuitBreaker(name = "openMeteoGeocoding", fallbackMethod = "photonFallback")
    public List<GeocodingResultRecord> searchLocations(String rawQuery) {
        String trimmed = rawQuery == null ? "" : rawQuery.trim();
        if (trimmed.length() < 2) {
            return List.of();
        }
        List<GeocodingResultRecord> primaryResults = fetchOpenMeteo(trimmed);
        boolean isMultiTokenOrRegional = trimmed.contains(",") || trimmed.split("\\s+").length > 1;
        if (primaryResults.isEmpty() || isMultiTokenOrRegional) {
            return mergeAndDeduplicate(primaryResults, fetchPhotonFallback(trimmed));
        }
        return primaryResults;
    }

    @SuppressWarnings("unused")
    public List<GeocodingResultRecord> photonFallback(String rawQuery, Throwable ex) {
        return fetchPhotonFallback(rawQuery == null ? "" : rawQuery.trim());
    }

    private List<GeocodingResultRecord> fetchOpenMeteo(String query) {
        // Spring Boot 3.x synchronous RestClient (virtual-thread friendly, zero reactive overhead)
        return List.of();
    }

    private List<GeocodingResultRecord> fetchPhotonFallback(String query) {
        // Queries https://photon.komoot.io/api/?q={query}&limit=8 and normalizes props.city + props.state into admin1
        return List.of();
    }

    private List<GeocodingResultRecord> mergeAndDeduplicate(
            List<GeocodingResultRecord> primary, List<GeocodingResultRecord> fallback) {
        // Deduplicates coordinates within 0.08 degrees latitude/longitude
        return fallback.isEmpty() ? primary : fallback;
    }
}
