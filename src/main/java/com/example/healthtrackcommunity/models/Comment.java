package com.example.healthtrackcommunity.models;

import com.google.firebase.database.Exclude;

import java.time.LocalDate;
import java.time.LocalTime;

public class Comment {

    private String id;
    private String patientId;
    private String doctorId;

    private String title;
    private String content;
    private boolean fromAlert;

    private String date;
    private String time;

    private LocalDate dateObj;
    private LocalTime timeObj;

    public Comment(String patientId, String doctorId, String title, String content, boolean fromAlert) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.title = title;
        this.content = content;
        this.fromAlert = fromAlert;

        dateObj = LocalDate.now();
        date = dateObj.toString();

        timeObj = LocalTime.now();
        time = timeObj.toString();
    }

    public Comment() {}

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isFromAlert() {
        return fromAlert;
    }

    public void setFromAlert(boolean fromAlert) {
        this.fromAlert = fromAlert;
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

        if (!(o instanceof Comment)) return false;

        Comment c = (Comment) o;

        if (c.getId() == null || c.getId().isEmpty()) return false;

        //si la clave es la misma es el mismo objeto, sin importar otros atributos
        return c.getId().equals(id);
    }
}
