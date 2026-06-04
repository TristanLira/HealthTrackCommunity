package com.example.healthtrackcommunity.models;

public class Prescription {

    private String id;
    private String patientId;
    private String name;
    private String frequency;
    private String instructions;

    public Prescription(String patientId, String name, String frequency, String instructions) {
        this.patientId = patientId;
        this.name = name;
        this.frequency = frequency;
        this.instructions = instructions;
    }

    private Prescription() {}

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if ( !(o instanceof Prescription) ) return false;

        Prescription p = (Prescription) o;

        if (p.getId() == null || p.getId().isEmpty()) return false;

        return p.getId().equals(id);
    }
}
