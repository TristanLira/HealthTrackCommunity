package com.example.healthtrackcommunity.models;

public class DoctorAccountRequest {

    private String id;
    private String email;
    private String password;
    private String name;
    private String specialization;

    public DoctorAccountRequest(String email, String password, String name, String specialization) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.specialization = specialization;
    }

    public DoctorAccountRequest() {}

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
        if (!(o instanceof DoctorAccountRequest)) return false;

        DoctorAccountRequest r = (DoctorAccountRequest) o;

        if (r.getId() == null || r.getId().isEmpty()) return false;

        //si se tiene el mismo id son iguales, sin importar ningún otro atributo
        return r.getId().equals(id);
    }
}
