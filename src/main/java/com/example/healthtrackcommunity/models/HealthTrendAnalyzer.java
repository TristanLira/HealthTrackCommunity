package com.example.healthtrackcommunity.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class HealthTrendAnalyzer {

    private final ObservableList<Metric> recent;

    private ObservableList<PressureMetric> pressure;
    private ObservableList<GlucoseMetric> glucose;
    private ObservableList<HeartRateMetric> heartRate;
    private ObservableList<WeightMetric> weight;

    private int dangerousPressureCounter;
    private int dangerousGlucoseCounter;
    private int dangerousHeartRateCounter;
    private int dangerousWeightCounter;

    public HealthTrendAnalyzer(ObservableList<Metric> recent) {
        this.recent = recent;
        pressure = FXCollections.observableArrayList();
        glucose = FXCollections.observableArrayList();
        heartRate = FXCollections.observableArrayList();
        weight = FXCollections.observableArrayList();

        getLists();

        analyzePressure();
        analyzeGlucose();
        analyzeHeartRate();
        analyzeWeight();
    }

    private void getLists() {
        for (Metric i: recent) {
            if (i instanceof PressureMetric) pressure.add((PressureMetric) i);
            else if (i instanceof GlucoseMetric) glucose.add((GlucoseMetric) i);
            else if (i instanceof HeartRateMetric) heartRate.add((HeartRateMetric) i);
            else if (i instanceof WeightMetric) weight.add((WeightMetric) i);
        }
    }

    /**** PRESIÓN ****/

    private void analyzePressure() {
        for (PressureMetric i: pressure) {
            if (pressureDangerous(i.getSystolic(), i.getDiastolic())) dangerousPressureCounter++;
        }
    }

    private boolean pressureDangerous(int systolic, int diastolic) {
        return (systolic > 140 || diastolic > 100
                || systolic < 80 || diastolic < 70);
    }

    /**** GLUCOSA ****/

    private void analyzeGlucose() {
        for (GlucoseMetric i: glucose) {
            if (glucoseDangerous(i.getGlucose())) dangerousGlucoseCounter++;
        }
    }

    private boolean glucoseDangerous(int glucose) {
        return (glucose > 200 || glucose < 70);
    }

    /**** FRECUENCIA CARDIACA ****/

    private void analyzeHeartRate() {
        for (HeartRateMetric i : heartRate) {
            if (heartRateDangerous(i.getHeartRate())) dangerousHeartRateCounter++;
        }
    }

    private boolean heartRateDangerous(int heartRate) {
        return (heartRate > 120 || heartRate < 50);
    }

    /**** PESO/IMC ****/

    private void analyzeWeight() {
        for (WeightMetric i : weight) {
            if (weightDangerous(i.getBmi())) dangerousWeightCounter++;
        }
    }

    private boolean weightDangerous(double bmi) {
        return (bmi >= 35 || bmi < 16);
    }

    public boolean hasDangerousTendencies() {
        return dangerousPressureCounter != 0 ||
                dangerousGlucoseCounter != 0 ||
                dangerousHeartRateCounter != 0 ||
                dangerousWeightCounter != 0;
    }
}
