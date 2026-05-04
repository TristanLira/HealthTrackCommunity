package com.example.healthtrackcommunity.models;

public class Patient {

    private String email;
    private String password;
    private String name;
    private String doctorEmail;

    public Patient(String email, String password, String doctorEmail, String name) {
        this.email = email;
        this.password = password;
        this.doctorEmail = doctorEmail;
        this.name = name;
    }

    public Patient() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDoctorEmail() {
        return doctorEmail;
    }

    public void setDoctorEmail(String doctorEmail) {
        this.doctorEmail = doctorEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Patient)) return false;

        Patient p = (Patient) o;

        //si la clave es la misma es el mismo objeto, sin importar otros atributos
        return p.getEmail().equals(email);
    }
}
