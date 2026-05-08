package com.example.healthtrackcommunity;

import com.example.healthtrackcommunity.models.Doctor;
import config.DoctorDAO;
import javafx.collections.ObservableList;

public class DoctorController {

    private DoctorDAO doctorDAO;
    private Doctor logged;

    public void initialize() {}

    public void setLoggedUser(DoctorDAO dao, Doctor logged) {
        this.logged = logged;
        this.doctorDAO = dao;
    }
}
