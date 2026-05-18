package com.example.healthtrackcommunity;

import com.example.healthtrackcommunity.controls.PatientDisplay;
import com.example.healthtrackcommunity.models.*;
import config.DoctorDAO;
import config.PatientDAO;
import config.MetricDAO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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

    public ScrollPane mainScrollPane;
    public StackPane mainContent;
    public Label doctorNameLabel;
    public Label doctorSpecializationLabel;

    //SECCIÓN DE DASHBOARD
    public VBox dashboardSection;
    public Label totalPatientsLabel;
    public Label activeAlertsLabel;
    public VBox alertPatientsContainer;

    //SECCION DE PACIENTES
    public VBox patientsListSection;
    public TextField searchPatientField;
    public Button filterPatientsList;
    public VBox patientsListContainer;

    //SECCIÓN DE SOLICITUDES
    public VBox pendingRequestsSection;
    public VBox requestsContainer;

    //SECCIÓN DE MÉTRICAS
    public VBox patientMetricsSection;
    public ComboBox<Patient> patientSelector;
    public VBox bloodPressureChartContainer;
    public VBox glucoseChartContainer;
    public VBox heartRateChartContainer;
    public VBox weightChartContainer;


    //DAOs y doctor loggeado
    private DoctorDAO doctorDAO;
    private Doctor logged;
    private PatientDAO patientDAO;

    private ObservableList<Patient> patients;

    public void initialize() {
    }

    public void setLoggedUser(DoctorDAO dao, Doctor logged) {
        this.logged = logged;
        this.doctorDAO = dao;
        this.patientDAO = new PatientDAO(logged);

        patients = patientDAO.getAll();

        doctorNameLabel.setText("Dr. " + logged.getName());
        doctorSpecializationLabel.setText(logged.getSpecialization());

        showPatients();
    }

    /******************************** MOSTRAR SECCIONES *****************************************/

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

    public void showReports(ActionEvent event) {
    }

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

    /******************************** MOSTRAR PACIENTES DEL DOCTOR *****************************************/

    private void showPatients() {
        patients.addListener((ListChangeListener<Patient>) change -> {
            while(change.next()) {

                if (change.wasAdded()) {

                    for (Patient i: change.getAddedSubList()) {
                        PatientDisplay p = new PatientDisplay(i);
                        Platform.runLater(() ->
                                patientsListContainer.getChildren().add(p));
                    }

                } else if (change.wasRemoved()) {

                    for (Patient i: change.getRemoved()) {
                        for (Node j: patientsListContainer.getChildren()) {
                            if (!(j instanceof PatientDisplay)) continue;
                            if ( ((PatientDisplay) j).displaysPatient(i) ) {
                                Platform.runLater(() ->
                                        patientsListContainer.getChildren().remove(j));
                                break;
                            }
                        }
                    }

                }
            }
        });
    }


    /********************* ALERTAS *******************************/

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
