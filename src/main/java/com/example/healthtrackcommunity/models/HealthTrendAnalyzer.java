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

    private double systolicAvg;
    private double diastolicAvg;
    private double glucoseAvg;
    private double heartRateAvg;
    private double bmiAvg;

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
        int ctr = 0;
        systolicAvg = 0;
        diastolicAvg = 0;

        for (PressureMetric i: pressure) {
            ctr++;
            systolicAvg += i.getSystolic();
            diastolicAvg += i.getDiastolic();

            if (pressureDangerous(i.getSystolic(), i.getDiastolic())) dangerousPressureCounter++;
        }

        systolicAvg /= (double) ctr;
        diastolicAvg /= (double) ctr;
    }

    private boolean pressureDangerous(int systolic, int diastolic) {
        return (systolic > 140 || diastolic > 100
                || systolic < 80 || diastolic < 70);
    }

    /**** GLUCOSA ****/

    private void analyzeGlucose() {
        int ctr = 0;
        glucoseAvg = 0;

        for (GlucoseMetric i: glucose) {
            ctr++;
            glucoseAvg += i.getGlucose();

            if (glucoseDangerous(i.getGlucose())) dangerousGlucoseCounter++;
        }

        glucoseAvg /= (double) ctr;
    }

    private boolean glucoseDangerous(int glucose) {
        return (glucose > 200 || glucose < 70);
    }

    /**** FRECUENCIA CARDIACA ****/

    private void analyzeHeartRate() {
        int ctr = 0;
        heartRateAvg = 0;

        for (HeartRateMetric i : heartRate) {
            ctr++;
            heartRateAvg += i.getHeartRate();

            if (heartRateDangerous(i.getHeartRate())) dangerousHeartRateCounter++;
        }

        heartRateAvg /= (double) ctr;
    }

    private boolean heartRateDangerous(int heartRate) {
        return (heartRate > 120 || heartRate < 50);
    }

    /**** PESO/IMC ****/

    private void analyzeWeight() {
        int ctr = 0;
        bmiAvg = 0;

        for (WeightMetric i : weight) {
            ctr++;
            bmiAvg += i.getBmi();

            if (weightDangerous(i.getBmi())) dangerousWeightCounter++;
        }

        bmiAvg /= (double) ctr;
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

    public MetricAlert getAlert(String patientId, String doctorId) {
        if (!hasDangerousTendencies()) return null;

        return new MetricAlert(patientId, doctorId,
                dangerousPressureCounter,
                dangerousGlucoseCounter,
                dangerousHeartRateCounter,
                dangerousWeightCounter);
    }

    //RECOMENDACIONES

    public String getPressureRecommendation() {

        if (pressure.isEmpty()) {
            return "No hay suficientes datos de presión arterial.";
        }

        if (dangerousPressureCounter >= 3) {
            return """
                Se detectaron múltiples lecturas de presión fuera de rango.
                Se recomienda monitoreo frecuente, reducir consumo de sodio
                y consultar con su médico.
                """;
        }

        if (systolicAvg >= 140 || diastolicAvg >= 90) {
            return """
                La presión promedio se encuentra elevada.
                Procura disminuir el estrés, moderar el sodio
                y mantener hidratación adecuada.
                """;
        }

        if (systolicAvg < 90 || diastolicAvg < 60) {
            return """
                La presión promedio se encuentra baja.
                Evita cambios bruscos de posicióny mantén buena hidratación.
                """;
        }

        return """
            La presión arterial se mantiene dentro de rangos saludables.
            """;
    }

    public String getGlucoseRecommendation() {

        if (glucose.isEmpty()) {
            return "No hay suficientes datos de glucosa.";
        }

        if (dangerousGlucoseCounter >= 3) {
            return """
                Se detectaron varias lecturas irregulares de glucosa.
                Se recomienda monitoreo continuo y mantener control alimenticio.
                """;
        }

        if (glucoseAvg > 160) {
            return """
                El promedio de glucosa es elevado.
                Reduce azúcares simples y mantén horarios de alimentación consistentes.
                """;
        }

        if (glucoseAvg < 80) {
            return """
                El promedio de glucosa es bajo.
                Evita ayunos prolongados y mantén comidas regulares.
                """;
        }

        return """
            Los niveles de glucosa se mantienen estables.
            Continúa con hábitos saludables.
            """;
    }

    public String getHeartRateRecommendation() {

        if (heartRate.isEmpty()) {
            return "No hay suficientes datos de ritmo cardiaco.";
        }

        if (dangerousHeartRateCounter >= 3) {
            return """
                Se detectaron múltiples variaciones importantes en la frecuencia cardiaca.
                Se recomienda descanso y monitoreo frecuente.
                """;
        }

        if (heartRateAvg > 100) {
            return """
                La frecuencia cardiaca promedio es elevada.
                Evita esfuerzos intensos y considera periodos de descanso.
                """;
        }

        if (heartRateAvg < 60) {
            return """
                La frecuencia cardiaca promedio es baja.
                Mantente alerta a síntomas como mareos o fatiga.
                """;
        }

        return """
            La frecuencia cardiaca se mantiene estable dentro de valores normales.
            """;
    }

    public String getWeightRecommendation() {

        if (weight.isEmpty()) {
            return "No hay suficientes datos de peso.";
        }

        if (dangerousWeightCounter >= 3) {
            return """
                Se detectaron múltiples mediciones de IMC fuera de rango saludable.
                Considera ajustes alimenticios y actividad física.
                """;
        }

        if (bmiAvg >= 30) {
            return """
                El IMC promedio es elevado.
                Se recomienda actividad física moderada y alimentación balanceada.
                """;
        }

        if (bmiAvg < 18.5) {
            return """
                El IMC promedio se encuentra bajo.
                Considera mejorar la ingesta nutricional y seguimiento médico si es necesario.
                """;
        }

        return """
            El peso corporal se mantiene dentro de un rango estable.
            """;
    }
}
