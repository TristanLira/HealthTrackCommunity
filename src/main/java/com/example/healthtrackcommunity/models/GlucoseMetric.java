package com.example.healthtrackcommunity.models;

import com.google.firebase.database.Exclude;

import java.time.LocalDate;
import java.time.LocalTime;

public class GlucoseMetric extends Metric {

    private int glucose;

    public GlucoseMetric(String userId, int glucose) {
        super();
        this.userId = userId;
        this.glucose = glucose;
    }

    public GlucoseMetric() {}

    public int getGlucose() {
        return glucose;
    }

    public void setGlucose(int glucose) {
        this.glucose = glucose;
    }

    public String toString() {
        return super.toString() + " (glucosa)";
    }
}
