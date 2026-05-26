package com.example.healthtrackcommunity;

import com.example.healthtrackcommunity.models.FamilyMember;
import com.example.healthtrackcommunity.models.Patient;
import config.DoctorDAO;
import config.FamilyMemberDAO;
import config.PatientDAO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.IllegalFormatCodePointException;

public class FamilyController {

    private static final Logger log = LoggerFactory.getLogger(FamilyController.class);
    public Label familyNameLabel;
    public ScrollPane mainScrollPane;
    public StackPane mainContent;
    
    //dashboard
    public VBox dashboardSection;
    public Label patientCountLabel;
    public Label lastAlertLabel;
    public VBox alertsContainer;
    public TextField patientEmailField;
    public PasswordField patientPasswordField;
    
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

    private FamilyMemberDAO familyDAO;
    private FamilyMember logged;

    private PatientDAO patientDAO;

    public void initialize() {}

    public void setLoggedUser(FamilyMemberDAO familyDAO, FamilyMember logged, PatientDAO patientDA0) {
        this.familyDAO = familyDAO;
        this.logged = logged;
        familyNameLabel.setText(logged.getName());

        this.patientDAO = patientDA0;
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

    /******************************* SECCIÓN DE DASHBOARD *****************************************/
    public void addPatient(ActionEvent actionEvent) {
        String patientEmail = patientEmailField.getText();
        String patientPassword = patientPasswordField.getText();

        cleanPatientForm();

        //hace la búsqueda en otro hilo para no bloquear el hilo de javafx
        Thread t = new Thread(() -> {
            Patient patient = null;
            for (Patient i: patientDAO.getAll()) {
                if (i.getEmail().equals(patientEmail) && i.getPassword().equals(patientPassword)) {
                    patient = i;
                    break;
                }
            }

            if (patient == null) {
                Platform.runLater(() ->
                    AlertUtil.showErrorAlert("Paciente no encontrado", "No se encontró el paciente, verifique el email y la contraseña."));
                return;
            }

            //si ya tiene el paciente evita registrarlo de nuevo
            if (logged.getPatientsId().contains(patient.getId())) {
                Platform.runLater(() ->
                    AlertUtil.showErrorAlert("Paciente registrado", "Ya ha registrado a este paciente."));
                return;
            }

            logged.addPatientId(patient.getId());
            //familyDAO.update(logged);

            familyDAO.update(logged,
                    () -> Platform.runLater(() ->
                            AlertUtil.showInfoAlert("Paciente agregado", "El paciente fue agregado correctamente a su cuenta.")),
                    () -> Platform.runLater(() ->
                            AlertUtil.showErrorAlert("Error", "El paciente no pudo ser agregado, inténtelo de nuevo."))
            );
        });

        t.start();
    }

    private void cleanPatientForm() {
        patientEmailField.clear();
        patientPasswordField.clear();
    }

    /******************************* SECCIÓN DE PACIENTES *****************************************/

    public void filterPatients(ActionEvent actionEvent) {
    }

    public void clearFilter(ActionEvent actionEvent) {
    }

    /******************************* SECCIÓN DE MÉTRICAS *****************************************/

    public void onPatientSelected(ActionEvent actionEvent) {
    }


}
