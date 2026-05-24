package com.example.healthtrackcommunity;

import com.example.healthtrackcommunity.controls.*;
import com.example.healthtrackcommunity.models.*;
import config.*;
import javafx.collections.FXCollections;
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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FamilyController {

    @FXML private VBox dashboardSection, patientsListSection, metricsSection, addPatientSection;
    @FXML private StackPane mainContent;
    @FXML private Label familyNameLabel, patientCountLabel, lastAlertLabel;
    @FXML private TextField searchPatientField;
    @FXML private VBox patientsContainer;
    @FXML private ComboBox<Patient> patientSelector;
    @FXML private TabPane metricsTabPane, chartsTabPane;
    @FXML private VBox pressureMetricsContainer, glucoseMetricsContainer, heartRateMetricsContainer, weightMetricsContainer;
    @FXML private VBox pressureChartContainer, glucoseChartContainer, heartRateChartContainer, weightChartContainer;
    @FXML private TextField patientEmailField, patientNameField;
    @FXML private PasswordField patientPasswordField;

    private FamilyMember logged;
    private FamilyMemberDAO familyDAO;
    private PatientDAO patientDAO;
    private ObservableList<Patient> allPatients;
    private ObservableList<Patient> associatedPatients;
    private Patient currentSelectedPatient;

    private Map<String, MetricDAO> metricDAOs = new HashMap<>();

    public void setLoggedUser(FamilyMemberDAO fDAO, PatientDAO pDAO, FamilyMember fm, AdminDAO aDAO, Admin a) {
        this.logged = fm;
        this.familyDAO = fDAO;
        this.patientDAO = pDAO;
        this.allPatients = patientDAO.getAll();
        this.associatedPatients = FXCollections.observableArrayList();
        familyNameLabel.setText(logged.getName());
        loadAssociatedPatients();
        setupMetricDAOs();
        showDashboard(null);
    }

    private void loadAssociatedPatients() {
        associatedPatients.clear();
        for (String pid : logged.getPatientIds()) {
            Patient p = patientDAO.get(pid);
            if (p != null) associatedPatients.add(p);
        }
        patientCountLabel.setText(String.valueOf(associatedPatients.size()));
        refreshPatientsList();
        patientSelector.setItems(associatedPatients);
    }

    private void refreshPatientsList() {
        patientsContainer.getChildren().clear();
        for (Patient p : associatedPatients) {
            HBox card = new HBox(15);
            card.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 8;");
            Label name = new Label(p.getName());
            name.setStyle("-fx-font-weight: bold;");
            Label email = new Label(p.getEmail());
            Button viewBtn = new Button("Ver métricas");
            viewBtn.setOnAction(e -> {
                currentSelectedPatient = p;
                loadMetricsForPatient(p);
                showMetrics(null);
            });
            card.getChildren().addAll(name, email, viewBtn);
            patientsContainer.getChildren().add(card);
        }
    }

    private void setupMetricDAOs() {
        // Se crearán cuando se seleccione un paciente
    }

    private void loadMetricsForPatient(Patient p) {
        MetricDAO pressureDAO = new MetricDAO(p, MetricDAO.PRESSURE);
        MetricDAO glucoseDAO = new MetricDAO(p, MetricDAO.GLUCOSE);
        MetricDAO heartDAO = new MetricDAO(p, MetricDAO.HEART_RATE);
        MetricDAO weightDAO = new MetricDAO(p, MetricDAO.WEIGHT);

        pressureMetricsContainer.getChildren().clear();
        glucoseMetricsContainer.getChildren().clear();
        heartRateMetricsContainer.getChildren().clear();
        weightMetricsContainer.getChildren().clear();

        for (Metric m : pressureDAO.getAll()) {
            if (m instanceof PressureMetric) pressureMetricsContainer.getChildren().add(new PressureDisplay((PressureMetric) m));
        }
        for (Metric m : glucoseDAO.getAll()) {
            if (m instanceof GlucoseMetric) glucoseMetricsContainer.getChildren().add(new GlucoseDisplay((GlucoseMetric) m));
        }
        for (Metric m : heartDAO.getAll()) {
            if (m instanceof HeartRateMetric) heartRateMetricsContainer.getChildren().add(new HeartRateDisplay((HeartRateMetric) m));
        }
        for (Metric m : weightDAO.getAll()) {
            if (m instanceof WeightMetric) weightMetricsContainer.getChildren().add(new WeightDisplay((WeightMetric) m));
        }

        // Cargar gráficos
        generateCharts(pressureDAO.getAll(), glucoseDAO.getAll(), heartDAO.getAll(), weightDAO.getAll());
    }

    private void generateCharts(ObservableList<Metric> pressureList, ObservableList<Metric> glucoseList,
                                ObservableList<Metric> heartList, ObservableList<Metric> weightList) {
        // Crear gráficos similares a DoctorController
        pressureChartContainer.getChildren().clear();
        glucoseChartContainer.getChildren().clear();
        heartRateChartContainer.getChildren().clear();
        weightChartContainer.getChildren().clear();

        if (!pressureList.isEmpty()) pressureChartContainer.getChildren().createLineChart(pressureList);
        // Implementar métodos auxiliares para crear gráficos...
    }

    @FXML public void showDashboard(ActionEvent e) { hideAll(); dashboardSection.setVisible(true); }
    @FXML public void showPatientsList(ActionEvent e) { hideAll(); patientsListSection.setVisible(true); }
    @FXML public void showMetrics(ActionEvent e) { if (currentSelectedPatient != null) { hideAll(); metricsSection.setVisible(true); } else showAlert("Selecciona un paciente primero."); }
    @FXML public void showAddPatient(ActionEvent e) { hideAll(); addPatientSection.setVisible(true); }

    @FXML public void addPatient(ActionEvent e) {
        String email = patientEmailField.getText().trim();
        String name = patientNameField.getText().trim();
        String password = patientPasswordField.getText();
        if (email.isEmpty() || name.isEmpty() || password.isEmpty()) {
            showAlert("Completa todos los campos.");
            return;
        }
        Patient newPatient = new Patient(email, password, name);
        patientDAO.create(newPatient, () -> {
            // Asociar paciente al familiar
            logged.getPatientIds().add(newPatient.getId());
            familyDAO.update(logged);
            loadAssociatedPatients();
            cancelAddPatient(null);
            showAlert("Paciente agregado exitosamente.");
        }, () -> showAlert("Error al crear paciente."), () -> showAlert("Email ya registrado."));
    }

    @FXML public void cancelAddPatient(ActionEvent e) {
        patientEmailField.clear(); patientNameField.clear(); patientPasswordField.clear();
        showDashboard(null);
    }

    @FXML public void filterPatients(ActionEvent e) {
        String filter = searchPatientField.getText().toLowerCase();
        for (Node card : patientsContainer.getChildren()) {
            boolean matches = card.getChildrenUnmodifiable().stream()
                    .anyMatch(node -> node instanceof Label && ((Label) node).getText().toLowerCase().contains(filter));
            card.setVisible(matches);
        }
    }

    @FXML public void clearFilter(ActionEvent e) {
        searchPatientField.clear();
        for (Node card : patientsContainer.getChildren()) card.setVisible(true);
    }

    @FXML public void onPatientSelected(ActionEvent e) {
        currentSelectedPatient = patientSelector.getValue();
        if (currentSelectedPatient != null) loadMetricsForPatient(currentSelectedPatient);
    }

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
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
        alert.showAndWait();
    }
}