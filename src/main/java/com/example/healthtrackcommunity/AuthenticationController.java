package com.example.healthtrackcommunity;

import config.DoctorDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AuthenticationController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    public void initialize() {
    }
}
