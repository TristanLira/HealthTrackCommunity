package com.example.healthtrackcommunity.models;

public class Doctor {

    private String id;
    private String email;
    private String password;
    private String name;
    private String specialization;

    public Doctor(String email, String password, String name, String specialization) {
        this.id = "";
        this.email = email;
        this.password = password;
        this.name = name;
        this.specialization = specialization;
    }

    public Doctor() {}

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

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Doctor)) return false;

        Doctor d = (Doctor) o;

        //si se tiene el mismo id son iguales, sin importar ningún otro atributo
        return d.getId().equals(id);
    }

    @Override
    public String toString() {
        return name + " (" + specialization + ")";
    }
}
