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

    public String getRecommendation() {

        String weather =
                currentWeather.toLowerCase();

        double temp =
                currentTemperature;

        // Tormenta
        if (weather.contains("tormenta")) {
            return """
                Se recomienda evitar actividades al aire libre y mantenerse resguardado.
                Procura desconectar dispositivos sensibles durante tormentas eléctricas.
                """;
        }

        // Lluvia
        if (weather.contains("lluvia")
                || weather.contains("chubascos")) {

            return """
                Se recomienda llevar paraguas o impermeable.
                Evita permanecer mucho tiempo en exteriores
                y ten precaución en superficies resbalosas.
                """;
        }

        // Neblina
        if (weather.contains("neblina")) {
            return """
                La visibilidad puede verse reducida.
                Ten precaución al conducir o realizar
                actividades al aire libre.
                """;
        }

        // Mucho calor
        if (temp >= 35) {
            return """
                Temperatura muy alta detectada.
                Mantente hidratado, evita exposición prolongada al sol
                y limita actividad física intensa.
                """;
        }

        // Calor moderado
        if (temp >= 28) {
            return """
                Clima cálido.
                Se recomienda beber suficiente agua,
                usar ropa ligera y evitar largas exposiciones al sol.
                """;
        }

        // Frío fuerte
        if (temp <= 8) {
            return """
                Temperatura baja detectada.
                Usa ropa abrigadora y evita cambios bruscos
                de temperatura.
                """;
        }

        // Frío ligero
        if (temp <= 15) {
            return """
                Clima fresco.
                Considera usar una prenda ligera
                y mantener una hidratación adecuada.
                """;
        }

        // Nublado
        if (weather.contains("nublado")) {
            return """
                Condiciones agradables para actividades moderadas.
                Mantén una hidratación adecuada durante el día.
                """;
        }

        // Despejado / default
        return """
            Condiciones climáticas estables.
            Mantén una hidratación adecuada
            y continúa con tus actividades normalmente.
            """;
    }
}