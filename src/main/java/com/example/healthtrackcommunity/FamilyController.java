package com.example.healthtrackcommunity;

import com.example.healthtrackcommunity.models.FamilyMember;
import com.example.healthtrackcommunity.models.Patient;
import config.DoctorDAO;
import config.FamilyMemberDAO;
import config.PatientDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class FamilyController {

    public Label familyNameLabel;
    public ScrollPane mainScrollPane;
    public StackPane mainContent;
    
    //dashboard
    public VBox dashboardSection;
    public Label patientCountLabel;
    public Label lastAlertLabel;
    public VBox alertsContainer;
    
    //pacientes
    public VBox patientsListSection;
    public TextField searchPatientField;
    public VBox patientsContainer;
    
    //métricas
    public VBox metricsSection;
    public ComboBox<Patient> patientSelector;
    public TabPane metricsTabPane;
    public VBox pressureMetricsContainer;
    public VBox glucoseMetricsContainer;
    public VBox heartRateMetricsContainer;
    public VBox weightMetricsContainer;

    public VBox chartsSection;
    public ComboBox<Patient> patientChartSelector;
    public TabPane chartsTabPane;
    public VBox pressureChartContainer;
    public VBox glucoseChartContainer;
    public VBox heartRateChartContainer;
    public VBox weightChartContainer;
    
    //agregar paciente
    public VBox addPatientSection;
    public TextField patientEmailField;
    public TextField patientNameField;
    public PasswordField patientPasswordField;



    public void initialize() {}

    public void setLoggedUser(FamilyMemberDAO familyDAO, FamilyMember logged, PatientDAO patientDA0) {
    }

    /******************************* MOSTRAR SECCIONES *****************************************/

    public void showDashboard(ActionEvent actionEvent) {
        hideAllSections();
        dashboardSection.setManaged(true);
        dashboardSection.setVisible(true);
    }

    public void showPatientsList(ActionEvent actionEvent) {
        hideAllSections();
        patientsListSection.setManaged(true);
        patientsListSection.setVisible(true);
    }

    public void showMetrics(ActionEvent actionEvent) {
        hideAllSections();
        metricsSection.setManaged(true);
        metricsSection.setVisible(true);
    }

    public void showCharts(ActionEvent actionEvent) {
        hideAllSections();
        chartsSection.setManaged(true);
        chartsSection.setVisible(true);
    }

    private void hideAllSections() {
        for (Node i: mainContent.getChildren()) {
            i.setManaged(false);
            i.setVisible(false);
        }
    }

    public void logout(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("authentication-view.fxml"));
        Parent root = loader.load();
        Scene currentScene = ((Node) event.getSource()).getScene();
        Stage stage = (Stage) currentScene.getWindow();
        stage.setScene(new Scene(root, currentScene.getWidth(), currentScene.getHeight()));
        stage.show();
    }

    /******************************* SECCIÓN DE PACIENTES *****************************************/

    public void filterPatients(ActionEvent actionEvent) {
    }

    public void clearFilter(ActionEvent actionEvent) {
    }

    /******************************* SECCIÓN DE MÉTRICAS *****************************************/

    public void onPatientSelected(ActionEvent actionEvent) {
    }

    /******************************* SECCIÓN DE MÉTRICAS *****************************************/
    public void addPatient(ActionEvent actionEvent) {
    }

    public void cancelAddPatient(ActionEvent actionEvent) {
    }
}
