package com.example.healthtrackcommunity.models;

import java.util.ArrayList;
import java.util.List;

public class FamilyMember {
    private String id;
    private String email;
    private String password;
    private String name;
    private List<String> patientIds;  // IDs de pacientes asociados

    public FamilyMember() {
        this.patientIds = new ArrayList<>();
    }

    public FamilyMember(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.patientIds = new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<String> getPatientIds() { return patientIds; }
    public void setPatientIds(List<String> patientIds) { this.patientIds = patientIds; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FamilyMember)) return false;
        FamilyMember that = (FamilyMember) o;
        return id != null && id.equals(that.id);
    }
}
