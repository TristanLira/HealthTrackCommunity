package com.example.healthtrackcommunity.models;

import com.google.firebase.database.Exclude;

import java.time.LocalDate;
import java.time.LocalTime;

public abstract class Metric {

    protected String id;
    protected String userId;
    protected String date;
    protected String time;
    protected LocalDate dateObj;
    protected LocalTime timeObj;

    public Metric() {
        id = "";
        userId = "";
        dateObj = LocalDate.now();
        timeObj = LocalTime.now();
        date = dateObj.toString();
        time = timeObj.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    //excluye los objetos de firebase, guardando únicamente sus equivalentes en string

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

        //if (!(o instanceof Metric)) return false;
        if (o == null || this.getClass() != o.getClass()) return false;

        Metric m = (Metric) o;

        if (m.getId() == null || m.getId().isEmpty()) return false;

        //si la clave es la misma es el mismo objeto, sin importar otros atributos
        return m.getId().equals(id);
    }
}
