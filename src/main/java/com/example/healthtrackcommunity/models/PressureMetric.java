package com.example.healthtrackcommunity.models;

import com.google.firebase.database.Exclude;
import java.time.LocalDate;
import java.time.LocalTime;

public class PressureMetric extends Metric {

    private int systolic;
    private int diastolic;

    public PressureMetric(String userId, int systolic, int diastolic) {
        super();
        this.userId = userId;
        this.systolic = systolic;
        this.diastolic = diastolic;
    }

    public PressureMetric() {}

    public int getSystolic() {
        return systolic;
    }

    public void setSystolic(int systolic) {
        this.systolic = systolic;
    }

    public int getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(int diastolic) {
        this.diastolic = diastolic;
    }

    public String toString() {
        return super.toString() + " (presion arterial)";
    }
}
