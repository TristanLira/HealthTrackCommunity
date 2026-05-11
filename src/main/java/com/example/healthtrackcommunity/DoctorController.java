package com.example.healthtrackcommunity;

import com.example.healthtrackcommunity.models.*;
import config.DoctorDAO;
import config.PatientDAO;
import config.MetricDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DoctorController {

    @FXML public Label doctorNameLabel;
    @FXML public Label doctorSpecializationLabel;
    @FXML public Label totalPatientsLabel;
    @FXML public Label activeAlertsLabel;
    @FXML public Label pendingConsultationsLabel;
    @FXML public ListView<Patient> alertPatientsListView;
    @FXML public TextField searchPatientField;
    @FXML public VBox dashboardSection;
    @FXML public VBox patientsListSection;
    @FXML public VBox pendingRequestsSection;
    @FXML public VBox patientMetricsSection;
    @FXML public ComboBox<Patient> patientSelector;
    @FXML public VBox bloodPressureChartContainer;
    @FXML public VBox glucoseChartContainer;
    @FXML public VBox heartRateChartContainer;
    @FXML public VBox weightChartContainer;
    @FXML public ScrollPane mainScrollPane;
    @FXML public StackPane mainContent;

    private DoctorDAO doctorDAO;
    private Doctor logged;
    private PatientDAO patientDAO;

    public void initialize() {
    }

    public void setLoggedUser(DoctorDAO dao, Doctor logged) {
        this.logged = logged;
        this.doctorDAO = dao;
        this.patientDAO = new PatientDAO();

        doctorNameLabel.setText("Dr. " + logged.getName());
        doctorSpecializationLabel.setText(logged.getSpecialization());
    }


    private void showPatientMetrics(Patient patient) {
        hideAllSections();
        patientMetricsSection.setVisible(true);
        patientMetricsSection.setManaged(true);
    }

    public void showDashboard(ActionEvent event) {
        hideAllSections();
        dashboardSection.setVisible(true);
        dashboardSection.setManaged(true);
    }

    public void showPatientsList(ActionEvent event) {
        hideAllSections();
        patientsListSection.setVisible(true);
        patientsListSection.setManaged(true);
    }

    public void showPendingRequests(ActionEvent event) {
        hideAllSections();
        pendingRequestsSection.setVisible(true);
        pendingRequestsSection.setManaged(true);
    }

    public void showPatientMetrics(ActionEvent event) {
        hideAllSections();
        patientMetricsSection.setVisible(true);
        patientMetricsSection.setManaged(true);
    }

    @FXML
    public void showReports(ActionEvent event) {
    }

    @FXML
    public void logout(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("authentication-view.fxml"));
        Parent root = loader.load();
        Scene currentScene = ((Node) event.getSource()).getScene();
        Stage stage = (Stage) currentScene.getWindow();
        stage.setScene(new Scene(root, currentScene.getWidth(), currentScene.getHeight()));
        stage.show();
    }

    private void hideAllSections() {
        for (Node i: mainContent.getChildren()) {
            i.setManaged(false);
            i.setVisible(false);
        }
    }

    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.show();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.show();
    }
}
