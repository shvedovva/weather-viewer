package com.example.weatherviewer.service;

import com.example.weatherviewer.dto.LocationSearchDto;
import com.example.weatherviewer.dto.WeatherDto;
import com.example.weatherviewer.exception.WeatherApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WeatherApiService {
    private static final String BASE_URL = "https://api.openweathermap.org";
    private static final String GEO_API_PATH = "/geo/1.0/direct";
    private static final String WEATHER_API_PATH = "/data/2.5/weather";

    @Value("${weather.api-key}")
    private String apiKey;

    private final WebClient webClient;

    public WeatherApiService() {
        this.webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .build();
    }

    public WeatherApiService(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<LocationSearchDto> searchLocations(String query) {
        try {
            LocationSearchDto[] locations = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                                    .path(GEO_API_PATH)
                                    .queryParam("q", query)
                                    .queryParam("limit", 5)
                                    .queryParam("appid", apiKey)
                                    .build())
                    .retrieve()
                    .bodyToMono(LocationSearchDto[].class)
                    .block();
            return locations != null ? List.of(locations) : List.of();
        } catch (WebClientResponseException e) {
            handleApiError(e);
            return List.of();
        } catch (Exception e) {
            throw new WeatherApiException("Failed to search locations: " + e.getMessage(), e);
        }
    }

    public WeatherDto getWeather(BigDecimal latitude, BigDecimal longitude) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(WEATHER_API_PATH)
                            .queryParam("lat", latitude)
                            .queryParam("lon", longitude)
                            .queryParam("units", "metric")
                            .queryParam("appid", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(WeatherDto.class)
                    .block();
        } catch (WebClientResponseException e) {
            handleApiError(e);
            return null;
        } catch (Exception e) {
            throw new WeatherApiException("Failed to get weather data: " + e.getMessage(), e);
        }
    }

    private void handleApiError(WebClientResponseException e) {
        HttpStatusCode status =  e.getStatusCode();
        String message = "Weather API error: " + status.value();

        if (status == HttpStatus.UNAUTHORIZED) {
            message = "Invalid API key";
        } else if (status == HttpStatus.TOO_MANY_REQUESTS) {
            message = "API rate limit exceeded";
        } else if (status.is4xxClientError()) {
            message = "Invalid request to weather API";
        } else if (status.is5xxServerError()) {
            message = "Weather service is temporarily unavailable";
        }

        throw new WeatherApiException(message, e);
    }
}
