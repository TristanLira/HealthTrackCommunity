package com.example.healthtrackcommunity;

import com.example.healthtrackcommunity.models.Patient;
import config.PatientDAO;

public class PatientController {

    private PatientDAO patientDAO;
    private Patient logged;

    public void initialize() {}

    public void setLoggedUser(PatientDAO dao, Patient logged) {
        this.logged = logged;
        this.patientDAO = dao;
    }

}
