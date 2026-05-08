package com.example.healthtrackcommunity.models;

public class Patient {

    private String id;
    private String email;
    private String password;
    private String name;
    private String doctorId;

    /*Al crear un paciente este no se vinculará automáticamente al médico que realizará el seguimiento, sino que se debe de enviar una solicitud
    * que el médico tendrá que aceptar, y hasta que sea aceptada el paciente se vinculará al médico dentro de la base de datos*/

    public Patient(String email, String password, String name) {
        this.id = "";
        this.email = email;
        this.password = password;
        this.name = name;
        doctorId = "";
    }

    //para firebase
    public Patient() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    @Override
    public String toString() {
        return "Paciente: " + name + "\n" + email + "\n" + password;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Patient)) return false;

        Patient p = (Patient) o;

        if (p.getId() == null || p.getId().isEmpty()) return false;

        //si la clave es la misma es el mismo objeto, sin importar otros atributos
        return p.getId().equals(id);
    }
}
