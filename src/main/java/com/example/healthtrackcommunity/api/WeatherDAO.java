package com.example.healthtrackcommunity.api;

import com.example.healthtrackcommunity.models.WeatherData;
import com.example.healthtrackcommunity.models.WeatherDay;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class WeatherDAO {

    private static final HttpClient client =
            HttpClient.newHttpClient();

    public WeatherData getWeather(
            double latitude,
            double longitude,
            int pastDays
    ) throws IOException, InterruptedException {

        /*String url =
                "https://api.open-meteo.com/v1/forecast"
                        + "?latitude=" + latitude
                        + "&longitude=" + longitude
                        + "&current=temperature_2m,weather_code"
                        + "&daily=temperature_2m_max,"
                        + "temperature_2m_min,"
                        + "weather_code"
                        + "&past_days=" + pastDays
                        + "&timezone=auto";*/

        //para que solo aparezcan hoy y los días pasados
        String url =
                "https://api.open-meteo.com/v1/forecast"
                        + "?latitude=" + latitude
                        + "&longitude=" + longitude
                        + "&current=temperature_2m,weather_code"
                        + "&daily=temperature_2m_max,"
                        + "temperature_2m_min,"
                        + "weather_code"
                        + "&past_days=" + pastDays
                        + "&forecast_days=1"
                        + "&timezone=auto";

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();

        // CURRENT
        JsonObject current = root.getAsJsonObject("current");

        double currentTemp = current.get("temperature_2m").getAsDouble();

        int currentCode =
                current.get("weather_code")
                        .getAsInt();

        String currentWeather =
                weatherDescription(currentCode);

        // DAILY HISTORY
        JsonObject daily = root.getAsJsonObject("daily");

        JsonArray dates = daily.getAsJsonArray("time");

        JsonArray maxTemps = daily.getAsJsonArray("temperature_2m_max");

        JsonArray minTemps = daily.getAsJsonArray("temperature_2m_min");

        JsonArray weatherCodes =
                daily.getAsJsonArray(
                        "weather_code");

        List<WeatherDay> history = new ArrayList<>();

        for (int i = 0; i < dates.size(); i++) {

            history.add(
                    new WeatherDay(
                            dates.get(i).getAsString(),
                            maxTemps.get(i).getAsDouble(),
                            minTemps.get(i).getAsDouble(),
                            weatherDescription(
                                    weatherCodes.get(i)
                                            .getAsInt()
                            )
                    )
            );
        }

        return new WeatherData(
                currentTemp,
                currentWeather,
                history
        );
    }

    private String weatherDescription(int code) {

        return switch (code) {

            case 0 ->
                    "Despejado";

            case 1, 2, 3 ->
                    "Nublado";

            case 45, 48 ->
                    "Neblina";

            case 51, 53, 55,
                 61, 63, 65 ->
                    "Lluvia";

            case 71, 73, 75 ->
                    "Nieve";

            case 80, 81, 82 ->
                    "Chubascos";

            case 95 ->
                    "Tormenta";

            default ->
                    "Desconocido";
        };
    }
}
