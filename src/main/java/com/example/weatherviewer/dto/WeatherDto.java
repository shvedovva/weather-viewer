package com.example.weatherviewer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherDto {
    private String name;

    @JsonProperty("main")
    private Main main;

    @JsonProperty("weather")
    private Weather[] weather;

    @JsonProperty("wind")
    private Wind wind;

    @JsonProperty("sys")
    private Sys sys;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Main {
        private BigDecimal temp;

        @JsonProperty("feels_like")
        private BigDecimal feelsLike;

        private BigDecimal humidity;
        private BigDecimal pressure;

        public BigDecimal getTemp() {
            return temp;
        }

        public void setTemp(BigDecimal temp) {
            this.temp = temp;
        }

        public BigDecimal getFeelsLike() {
            return feelsLike;
        }

        public void setFeelsLike(BigDecimal feelsLike) {
            this.feelsLike = feelsLike;
        }

        public BigDecimal getHumidity() {
            return humidity;
        }

        public void setHumidity(BigDecimal humidity) {
            this.humidity = humidity;
        }

        public BigDecimal getPressure() {
            return pressure;
        }

        public void setPressure(BigDecimal pressure) {
            this.pressure = pressure;
        }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weather {
        private String main;
        private String description;
        private String icon;

        public String getMain() {
            return main;
        }

        public void setMain(String main) {
            this.main = main;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Wind {
        private BigDecimal speed;

        public BigDecimal getSpeed() {
            return speed;
        }

        public void setSpeed(BigDecimal speed) {
            this.speed = speed;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sys {
        private String country;

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public void setWeather(Weather[] weather) {
        this.weather = weather;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Sys getSys() {
        return sys;
    }

    public void setSys(Sys sys) {
        this.sys = sys;
    }
}
