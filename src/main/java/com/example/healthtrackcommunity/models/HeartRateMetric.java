package com.example.healthtrackcommunity.models;

import com.google.firebase.database.Exclude;
import java.time.LocalDate;
import java.time.LocalTime;

public class HeartRateMetric extends Metric {

    private int heartRate;

    public HeartRateMetric(int heartRate) {
        super();
        this.heartRate = heartRate;
    }

    public HeartRateMetric() {}

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

}
