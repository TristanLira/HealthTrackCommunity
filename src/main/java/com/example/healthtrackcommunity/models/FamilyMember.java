package com.example.healthtrackcommunity.models;

public class FamilyMember {

    private String id;
    private String email;
    private String password;
    private String name;

    public FamilyMember(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public FamilyMember() {}

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
        if (!(o instanceof FamilyMember)) return false;

        FamilyMember d = (FamilyMember) o;

        //si no tiene id no se puede comparar
        if (d.getId() == null || d.getId().isEmpty()) return false;

        //si se tiene el mismo id son iguales, sin importar ningún otro atributo
        return d.getId().equals(id);
    }

}
