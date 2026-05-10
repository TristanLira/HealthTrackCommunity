package com.example.healthtrackcommunity.models;

public class MonitoringRequest {

    private String id;
    private String patientId;
    private String doctorId;

    public MonitoringRequest(String patientId, String doctorId) {
        this.doctorId = doctorId;
        this.patientId = patientId;
    }

    public MonitoringRequest() {}

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

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if ((o instanceof MonitoringRequest)) return false;

        MonitoringRequest m = (MonitoringRequest) o;

        if (m.getId() == null || m.getId().isEmpty()) return false;

        return m.getId().equals(id);
    }
}
