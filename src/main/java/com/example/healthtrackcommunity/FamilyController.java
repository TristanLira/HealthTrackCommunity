package com.example.healthtrackcommunity;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class FamilyController {

    public Label familyNameLabel;
    public ScrollPane mainScrollPane;
    public StackPane mainContent;
    
    //dashboard
    public VBox dashboardSection;
    public Label patientCountLabel;
    public Label lastAlertLabel;
    
    //pacientes
    public VBox patientsListSection;
    public TextField searchPatientField;
    public VBox patientsContainer;
    
    //métricas
    public VBox metricsSection;
    public ComboBox patientSelector;
    public TabPane metricsTabPane;
    public VBox pressureMetricsContainer;
    public VBox glucoseMetricsContainer;
    public VBox heartRateMetricsContainer;
    public VBox weightMetricsContainer;
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

    public void showAddPatient(ActionEvent actionEvent) {
        hideAllSections();
        addPatientSection.setManaged(true);
        addPatientSection.setVisible(true);
    }

    private void hideAllSections() {
        for (Node i: mainContent.getChildren()) {
            i.setManaged(false);
            i.setVisible(false);
        }
    }

    public void logout(ActionEvent actionEvent) {
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
