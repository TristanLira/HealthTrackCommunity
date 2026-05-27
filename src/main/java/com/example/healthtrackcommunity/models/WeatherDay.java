package com.example.healthtrackcommunity.models;

public class WeatherDay {

    private String date;
    private double maxTemperature;
    private double minTemperature;
    private String weather;

    public WeatherDay(String date,
                      double maxTemperature,
                      double minTemperature,
                      String weather) {
        this.date = date;
        this.maxTemperature = maxTemperature;
        this.minTemperature = minTemperature;
        this.weather = weather;
    }

    public String getDate() {
        return date;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public String getWeather() {
        return weather;
    }

    @Override
    public String toString() {
        return date + " | "
                + minTemperature + "°C - "
                + maxTemperature + "°C | "
                + weather;
    }
}
