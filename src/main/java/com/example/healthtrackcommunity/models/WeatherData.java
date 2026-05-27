package com.example.healthtrackcommunity.models;

import java.util.List;

public class WeatherData {

    private double currentTemperature;
    private String currentWeather;
    private List<WeatherDay> history;

    public WeatherData(double currentTemperature,
                       String currentWeather,
                       List<WeatherDay> history) {

        this.currentTemperature = currentTemperature;
        this.currentWeather = currentWeather;
        this.history = history;
    }

    public double getCurrentTemperature() {
        return currentTemperature;
    }

    public String getCurrentWeather() {
        return currentWeather;
    }

    public List<WeatherDay> getHistory() {
        return history;
    }
}