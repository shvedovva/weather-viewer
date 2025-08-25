package com.example.weatherviewer.service;


import com.example.weatherviewer.dto.LocationSearchDto;
import com.example.weatherviewer.dto.WeatherDto;
import com.example.weatherviewer.exception.WeatherApiException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class WeatherRestTemplateService {

    private static final String BASE_URL = "https://api.openweathermap.org";
    private static final String GEO_API_PATH = "/geo/1.0/direct";
    private static final String WEATHER_API_PATH = "/data/2.5/weather";

    @Value("${weather.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final JsonMapper jsonMapper;


    public WeatherRestTemplateService(RestTemplate restTemplate, JsonMapper jsonMapper) {
        this.restTemplate = restTemplate;
        this.jsonMapper = jsonMapper;
    }

    public List<LocationSearchDto> searchLocations(String query) {
        URI uri = UriComponentsBuilder.fromHttpUrl(BASE_URL + GEO_API_PATH)
                .queryParam("q", query.trim())
                .queryParam("limit", 5)
                .queryParam("appid", apiKey)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();
        String body = exchangeToString(uri);

        try{
            LocationSearchDto[] arr = jsonMapper.readValue(body, LocationSearchDto[].class);
            return arr != null ? List.of(arr) : List.of();
        } catch (IOException e) {
            throw new WeatherApiException("Failed to parse location search response", e);
        }
    }

    public WeatherDto getWeather(BigDecimal latitude, BigDecimal longitude){
        String lat = latitude.toPlainString();
        String lon = longitude.toPlainString();

        URI uri = UriComponentsBuilder.fromHttpUrl(BASE_URL + WEATHER_API_PATH)
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .queryParam("units", "metric")
                .queryParam("appid", apiKey)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();
        String body = exchangeToString(uri);

        try {
            return jsonMapper.readValue(body, WeatherDto.class);
        } catch (IOException e) {
            throw new WeatherApiException("Failed to parse weather response", e);
        }
    }

    private String exchangeToString(URI uri) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String.class);
        } catch (RestClientException e) {
            throw new WeatherApiException("HTTP error while calling weather API: " + e.getMessage(), e);
        }

        int status = response.getStatusCodeValue();
        String body =response.getBody() != null ? response.getBody() : "";

        if (status >= 200 && status < 300) {
            return body;
        } else {
            handleStatusError(status, body);
            return "";
        }
    }

    private void handleStatusError(int status, String body) {
        String message = "Weather API error: HTTP " + status;
        if (status == 401) {
            message = "Invalid API key";
        } else if (status == 429) {
            message = "API rate limit exceeded";
        } else if (status >= 400 && status < 500) {
            message = "Invalid request to weather API";
        } else if (status >= 500) {
            message = "Weather service is temporarily unavailable";
        }

        String bodySnippet = (body != null && !body.isBlank()) ? " Response body: " + truncate (body, 500) : "";
        throw new WeatherApiException(message + bodySnippet);
    }

    private static String truncate (String s, int maxLen) {
        if (s == null) return "";
        return  s.length() <= maxLen ? s : s.substring(0, maxLen) + "...";
    }
}
