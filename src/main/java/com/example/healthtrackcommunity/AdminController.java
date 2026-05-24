package com.example.healthtrackcommunity;

import com.example.healthtrackcommunity.models.*;
import config.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

public class AdminController {

    @FXML private VBox dashboardSection, pendingDoctorsSection, allDoctorsSection, allPatientsSection;
    @FXML private StackPane mainContent;
    @FXML private Label totalDoctorsLabel, totalPatientsLabel, pendingDoctorsLabel;
    @FXML private VBox pendingDoctorsContainer, allDoctorsContainer, allPatientsContainer;
    @FXML private TextField searchDoctorField, searchPatientField;
    @FXML private Label adminNameLabel;

    private Admin logged;
    private AdminDAO adminDAO;
    private PatientDAO patientDAO;
    private DoctorDAO doctorDAO;
    private ObservableList<Doctor> allDoctors, pendingDoctors;
    private ObservableList<Patient> allPatients;

    public void setLoggedUser(AdminDAO aDAO, Admin admin, PatientDAO pDAO, DoctorDAO dDAO) {
        this.logged = admin;
        this.adminDAO = aDAO;
        this.patientDAO = pDAO;
        this.doctorDAO = dDAO;
        adminNameLabel.setText(logged.getName());
        allDoctors = doctorDAO.getAll();
        allPatients = patientDAO.getAll();
        updateDashboard();
        showDashboard(null);
    }

    private void updateDashboard() {
        totalDoctorsLabel.setText(String.valueOf(allDoctors.size()));
        totalPatientsLabel.setText(String.valueOf(allPatients.size()));
        pendingDoctors = doctorDAO.getPendingDoctors();
        pendingDoctorsLabel.setText(String.valueOf(pendingDoctors.size()));
    }

    private void refreshPendingDoctorsList() {
        pendingDoctorsContainer.getChildren().clear();
        for (Doctor d : pendingDoctors) {
            HBox card = new HBox(15);
            card.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 8;");
            Label info = new Label(d.getName() + " - " + d.getEmail() + " - " + d.getSpecialization());
            Button approveBtn = new Button("Aprobar");
            approveBtn.getStyleClass().add("boton-aprobar");
            approveBtn.setOnAction(e -> {
                d.setApproved(true);
                doctorDAO.update(d);
                refreshPendingDoctorsList();
                updateDashboard();
            });
            card.getChildren().addAll(info, approveBtn);
            pendingDoctorsContainer.getChildren().add(card);
        }
    }

    private void refreshAllDoctorsList() {
        allDoctorsContainer.getChildren().clear();
        for (Doctor d : allDoctors) {
            Label lbl = new Label(d.getName() + " | " + d.getEmail() + " | " + d.getSpecialization() + (d.isApproved() ? " (Aprobado)" : " (Pendiente)"));
            lbl.setStyle("-fx-padding: 5;");
            allDoctorsContainer.getChildren().add(lbl);
        }
    }

    private void refreshAllPatientsList() {
        allPatientsContainer.getChildren().clear();
        for (Patient p : allPatients) {
            Label lbl = new Label(p.getName() + " | " + p.getEmail() + " | Doctor asignado: " +
                    (p.getDoctorId() == null || p.getDoctorId().isEmpty() ? "Ninguno" : p.getDoctorId()));
            lbl.setStyle("-fx-padding: 5;");
            allPatientsContainer.getChildren().add(lbl);
        }
    }

    @FXML public void showDashboard(ActionEvent e) { hideAll(); dashboardSection.setVisible(true); }
    @FXML public void showPendingDoctors(ActionEvent e) { hideAll(); refreshPendingDoctorsList(); pendingDoctorsSection.setVisible(true); }
    @FXML public void showAllDoctors(ActionEvent e) { hideAll(); refreshAllDoctorsList(); allDoctorsSection.setVisible(true); }
    @FXML public void showAllPatients(ActionEvent e) { hideAll(); refreshAllPatientsList(); allPatientsSection.setVisible(true); }

    @FXML public void logout(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("authentication-view.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void hideAll() {
        for (Node n : mainContent.getChildren()) { n.setVisible(false); n.setManaged(false); }
    }
}
