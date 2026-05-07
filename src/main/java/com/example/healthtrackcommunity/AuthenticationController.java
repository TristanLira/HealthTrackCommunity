package com.example.healthtrackcommunity;

import config.DoctorDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class AuthenticationController {

    public StackPane formsSection;
    public Button showPatientRegisterBtn;
    public Button showDoctorRegisterBtn;
    public Button showLoginBtn;

    //formulario de registro para doctor
    public VBox doctorRegisterForm;
    public TextField doctorEmailField;
    public TextField doctorNameField;
    public TextField doctorSpecialityField;
    public TextField doctorPasswordField;
    public Button registerDoctorBtn;

    //formulario de registro para paciente
    public VBox patientRegisterForm;
    public TextField patientEmailField;
    public TextField patientNameField;
    public TextField patientPasswordField;
    public Button registerPatientBtn;

    //formulario de login
    public VBox userLoginForm;
    public TextField loginEmailField;
    public TextField loginPasswordField;
    public Button loginBtn;

    public void initialize() {
        showForm(userLoginForm);
    }

    /*MOSTRAR FORMULARIOS DE REGISTRO/INICIO DE SESION*/
    public void showDoctorRegister(ActionEvent actionEvent) {
        showForm(doctorRegisterForm);
    }

    public void showPatientRegister(ActionEvent actionEvent) {
        showForm(patientRegisterForm);
    }

    public void showLogin(ActionEvent actionEvent) {
        showForm(userLoginForm);
    }

    private void showForm(Node showed) {
        for(Node i: formsSection.getChildren()) {
            i.setManaged(false);
            i.setVisible(false);
        }

        showed.setVisible(true);
        showed.setManaged(true);
    }
}
