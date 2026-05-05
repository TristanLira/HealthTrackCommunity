package com.example.healthtrackcommunity.models;

public class Patient {

    private String email;
    private String password;
    private String name;
    private String doctorEmail;

    /*Al crear un paciente este no se vinculará automáticamente al médico que realizará el seguimiento, sino que se debe de enviar una solicitud
    * que el médico tendrá que aceptar, y hasta que sea aceptada el paciente se vinculará al médico dentro de la base de datos*/

    public Patient(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
        doctorEmail = "";
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
