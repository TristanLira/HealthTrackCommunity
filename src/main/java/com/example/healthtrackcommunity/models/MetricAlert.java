package com.example.healthtrackcommunity.models;

import com.google.firebase.database.Exclude;

import java.time.LocalDate;
import java.time.LocalTime;

public class MetricAlert {

    private String id;
    private String patientId;
    private String doctorId;

    private int dangerousPressureCounter;
    private int dangerousGlucoseCounter;
    private int dangerousHeartRateCounter;
    private int dangerousWeightCounter;

    private String date;
    private String time;
    private LocalDate dateObj;
    private LocalTime timeObj;

    public MetricAlert(String patientId, String doctorId,
                       int dangerousPressureCounter,
                       int dangerousGlucoseCounter,
                       int dangerousHeartRateCounter,
                       int dangerousWeightCounter) {

        this.patientId = patientId;
        this.doctorId = doctorId;

        this.dangerousPressureCounter = dangerousPressureCounter;
        this.dangerousGlucoseCounter = dangerousGlucoseCounter;
        this.dangerousHeartRateCounter = dangerousHeartRateCounter;
        this.dangerousWeightCounter = dangerousWeightCounter;

        dateObj = LocalDate.now();
        timeObj = LocalTime.now();
        date = dateObj.toString();
        time = timeObj.toString();
    }

    public MetricAlert() {}


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public int getDangerousPressureCounter() {
        return dangerousPressureCounter;
    }

    public void setDangerousPressureCounter(int dangerousPressureCounter) {
        this.dangerousPressureCounter = dangerousPressureCounter;
    }

    public int getDangerousGlucoseCounter() {
        return dangerousGlucoseCounter;
    }

    public void setDangerousGlucoseCounter(int dangerousGlucoseCounter) {
        this.dangerousGlucoseCounter = dangerousGlucoseCounter;
    }

    public int getDangerousHeartRateCounter() {
        return dangerousHeartRateCounter;
    }

    public void setDangerousHeartRateCounter(int dangerousHeartRateCounter) {
        this.dangerousHeartRateCounter = dangerousHeartRateCounter;
    }

    public int getDangerousWeightCounter() {
        return dangerousWeightCounter;
    }

    public void setDangerousWeightCounter(int dangerousWeightCounter) {
        this.dangerousWeightCounter = dangerousWeightCounter;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
        dateObj = LocalDate.parse(date);
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
        timeObj = LocalTime.parse(time);
    }

    @Exclude
    public LocalDate getDateObj() {
        return dateObj;
    }

    @Exclude
    public LocalTime getTimeObj() {
        return timeObj;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;

        if (!(o instanceof MetricAlert)) return false;

        MetricAlert m = (MetricAlert) o;

        if (m.getId() == null || m.getId().isEmpty()) return false;

        //si la clave es la misma es el mismo objeto, sin importar otros atributos
        return m.getId().equals(id);
    }
}
