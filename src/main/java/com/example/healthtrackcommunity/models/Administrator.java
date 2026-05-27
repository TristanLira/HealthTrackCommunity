package com.example.healthtrackcommunity.models;

import config.AdministratorDAO;

public class Administrator {

    private String id;
    private String email;
    private String password;
    private String name;

    public Administrator(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public Administrator() {}

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

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Administrator)) return false;

        Administrator a = (Administrator) o;

        if (a.getId() == null || a.getId().isEmpty()) return false;

        //si se tiene el mismo id son iguales, sin importar ningún otro atributo
        return a.getId().equals(id);
    }
}
