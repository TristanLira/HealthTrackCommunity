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
    @FXML public Label pendingRequestsBadge;
    @FXML public Label totalPatientsLabel;
    @FXML public Label activeAlertsLabel;
    @FXML public Label pendingConsultationsLabel;
    @FXML public ListView<Patient> alertPatientsListView;
    @FXML public ListView<Patient> patientsListView;
    @FXML public ListView<Patient> pendingRequestsListView;
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
    @FXML public TableView<PressureMetric> bloodPressureTable;
    @FXML public TableView<GlucoseMetric> glucoseTable;
    @FXML public TableView<HeartRateMetric> heartRateTable;
    @FXML public TableView<WeightMetric> weightTable;
    @FXML public ScrollPane mainScrollPane;
    @FXML public StackPane mainContent;

    private DoctorDAO doctorDAO;
    private Doctor logged;
    private PatientDAO patientDAO;
    private ObservableList<Patient> myPatients;
    private ObservableList<Patient> pendingRequests;
    private Map<String, Patient> selectedPatientCache;
    private Map<String, MetricDAO> patientMetricDAOs;
    private Patient currentSelectedPatient;

    public void initialize() {
        selectedPatientCache = new HashMap<>();
        patientMetricDAOs = new HashMap<>();
        setupPatientListCellFactory();
        setupPendingRequestsCellFactory();
        setupAlertListCellFactory();
        setupPatientSelector();

        searchPatientField.textProperty().addListener((obs, old, val) -> filterPatients(val));
    }

    private void setupPatientListCellFactory() {
        patientsListView.setCellFactory(lv -> new ListCell<Patient>() {
            @Override
            protected void updateItem(Patient patient, boolean empty) {
                super.updateItem(patient, empty);
                if (empty || patient == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(patient.getName() + " - " + patient.getEmail());
                    setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2) {
                            showPatientMetrics(patient);
                        }
                    });
                }
            }
        });
    }

    private void setupPendingRequestsCellFactory() {
        pendingRequestsListView.setCellFactory(lv -> new ListCell<Patient>() {
            @Override
            protected void updateItem(Patient patient, boolean empty) {
                super.updateItem(patient, empty);
                if (empty || patient == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Button acceptBtn = new Button("Aceptar");
                    Button rejectBtn = new Button("Rechazar");
                    acceptBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
                    rejectBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                    acceptBtn.setOnAction(e -> acceptRequest(patient));
                    rejectBtn.setOnAction(e -> rejectRequest(patient));

                    javafx.scene.layout.HBox hbox = new javafx.scene.layout.HBox(10,
                            new Label(patient.getName() + " - " + patient.getEmail()),
                            acceptBtn, rejectBtn);
                    setGraphic(hbox);
                }
            }
        });
    }

    private void setupAlertListCellFactory() {
        alertPatientsListView.setCellFactory(lv -> new ListCell<Patient>() {
            @Override
            protected void updateItem(Patient patient, boolean empty) {
                super.updateItem(patient, empty);
                if (empty || patient == null) {
                    setText(null);
                } else {
                    setText(patient.getName() + " - Tiene alertas activas");
                    setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2) {
                            showPatientMetrics(patient);
                        }
                    });
                }
            }
        });
    }

    private void setupPatientSelector() {
        patientSelector.setCellFactory(lv -> new ListCell<Patient>() {
            @Override
            protected void updateItem(Patient patient, boolean empty) {
                super.updateItem(patient, empty);
                if (empty || patient == null) {
                    setText(null);
                } else {
                    setText(patient.getName() + " - " + patient.getEmail());
                }
            }
        });

        patientSelector.setButtonCell(new ListCell<Patient>() {
            @Override
            protected void updateItem(Patient patient, boolean empty) {
                super.updateItem(patient, empty);
                if (empty || patient == null) {
                    setText("Seleccionar paciente");
                } else {
                    setText(patient.getName());
                }
            }
        });

        patientSelector.valueProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                loadPatientMetricsData(selected);
            }
        });
    }

    public void setLoggedUser(DoctorDAO dao, Doctor logged) {
        this.logged = logged;
        this.doctorDAO = dao;
        this.patientDAO = new PatientDAO();

        doctorNameLabel.setText("Dr. " + logged.getName());
        doctorSpecializationLabel.setText(logged.getSpecialization());

        loadMyPatients();
        loadPendingRequests();
        updateDashboard();
        updatePatientSelector();
    }

    private void loadMyPatients() {
        myPatients = FXCollections.observableArrayList();
        for (Patient p : patientDAO.getAll()) {
            if (p.getDoctorId() != null && p.getDoctorId().equals(logged.getId())) {
                myPatients.add(p);
            }
        }
        patientsListView.setItems(myPatients);
    }

    private void loadPendingRequests() {
        pendingRequests = FXCollections.observableArrayList();
        for (Patient p : patientDAO.getAll()) {
            if ((p.getDoctorId() == null || p.getDoctorId().isEmpty()) && !pendingRequests.contains(p)) {
                pendingRequests.add(p);
            }
        }
        pendingRequestsListView.setItems(pendingRequests);

        int count = pendingRequests.size();
        if (count > 0) {
            pendingRequestsBadge.setText(String.valueOf(count));
            pendingRequestsBadge.setVisible(true);
        } else {
            pendingRequestsBadge.setVisible(false);
        }
    }

    private void updateDashboard() {
        totalPatientsLabel.setText(String.valueOf(myPatients.size()));

        int alerts = 0;
        alertPatientsListView.getItems().clear();

        for (Patient p : myPatients) {
            if (hasActiveAlerts(p)) {
                alerts++;
                alertPatientsListView.getItems().add(p);
            }
        }
        activeAlertsLabel.setText(String.valueOf(alerts));
        pendingConsultationsLabel.setText("0");
    }

    private void updatePatientSelector() {
        patientSelector.setItems(myPatients);
    }

    private boolean hasActiveAlerts(Patient p) {
        MetricDAO glucoseDAO = new MetricDAO(p, MetricDAO.GLUCOSE);
        MetricDAO pressureDAO = new MetricDAO(p, MetricDAO.PRESSURE);

        if (!glucoseDAO.getAll().isEmpty()) {
            GlucoseMetric last = (GlucoseMetric) glucoseDAO.getAll().get(glucoseDAO.getAll().size() - 1);
            if (last.getGlucose() > 140 || last.getGlucose() < 70) return true;
        }

        if (!pressureDAO.getAll().isEmpty()) {
            PressureMetric last = (PressureMetric) pressureDAO.getAll().get(pressureDAO.getAll().size() - 1);
            if (last.getSystolic() > 140 || last.getDiastolic() > 90) return true;
        }

        return false;
    }

    private void filterPatients(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            patientsListView.setItems(myPatients);
        } else {
            ObservableList<Patient> filtered = FXCollections.observableArrayList();
            for (Patient p : myPatients) {
                if (p.getName().toLowerCase().contains(searchText.toLowerCase())) {
                    filtered.add(p);
                }
            }
            patientsListView.setItems(filtered);
        }
    }

    private void acceptRequest(Patient patient) {
        patient.setDoctorId(logged.getId());
        patientDAO.update(patient);
        pendingRequests.remove(patient);
        myPatients.add(patient);
        updateDashboard();
        updatePatientSelector();

        int count = pendingRequests.size();
        if (count > 0) {
            pendingRequestsBadge.setText(String.valueOf(count));
        } else {
            pendingRequestsBadge.setVisible(false);
        }

        showInfoAlert("Solicitud aceptada", "El paciente " + patient.getName() + " ha sido asignado a tu lista.");
    }

    private void rejectRequest(Patient patient) {
        pendingRequests.remove(patient);
        showInfoAlert("Solicitud rechazada", "El paciente " + patient.getName() + " no ha sido asignado.");
    }

    private void showPatientMetrics(Patient patient) {
        currentSelectedPatient = patient;
        selectedPatientCache.put(patient.getId(), patient);
        patientSelector.getSelectionModel().select(patient);
        loadPatientMetricsData(patient);
        hideAllSections();
        patientMetricsSection.setVisible(true);
    }

    private void loadPatientMetricsData(Patient patient) {
        if (patient == null) return;

        MetricDAO glucoseDAO = new MetricDAO(patient, MetricDAO.GLUCOSE);
        MetricDAO pressureDAO = new MetricDAO(patient, MetricDAO.PRESSURE);
        MetricDAO heartRateDAO = new MetricDAO(patient, MetricDAO.HEART_RATE);
        MetricDAO weightDAO = new MetricDAO(patient, MetricDAO.WEIGHT);

        glucoseTable.setItems((ObservableList<GlucoseMetric>) (ObservableList<?>) glucoseDAO.getAll());
        bloodPressureTable.setItems((ObservableList<PressureMetric>) (ObservableList<?>) pressureDAO.getAll());
        heartRateTable.setItems((ObservableList<HeartRateMetric>) (ObservableList<?>) heartRateDAO.getAll());
        weightTable.setItems((ObservableList<WeightMetric>) (ObservableList<?>) weightDAO.getAll());

        createGlucoseChart(glucoseDAO.getAll());
        createPressureChart(pressureDAO.getAll());
        createHeartRateChart(heartRateDAO.getAll());
        createWeightChart(weightDAO.getAll());
    }

    private void createGlucoseChart(ObservableList<Metric> data) {
        glucoseChartContainer.getChildren().clear();
        if (data.isEmpty()) {
            glucoseChartContainer.getChildren().add(new Label("No hay datos de glucosa"));
            return;
        }

        BarChart<String, Number> chart = new BarChart<>(new javafx.scene.chart.CategoryAxis(), new javafx.scene.chart.NumberAxis());
        chart.setTitle("Niveles de Glucosa");
        chart.setPrefHeight(300);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Glucosa (mg/dL)");

        for (Metric m : data) {
            GlucoseMetric g = (GlucoseMetric) m;
            series.getData().add(new XYChart.Data<>(g.getDate(), g.getGlucose()));
        }

        chart.getData().add(series);
        glucoseChartContainer.getChildren().add(chart);
    }

    private void createPressureChart(ObservableList<Metric> data) {
        bloodPressureChartContainer.getChildren().clear();
        if (data.isEmpty()) {
            bloodPressureChartContainer.getChildren().add(new Label("No hay datos de presión arterial"));
            return;
        }

        BarChart<String, Number> chart = new BarChart<>(new javafx.scene.chart.CategoryAxis(), new javafx.scene.chart.NumberAxis());
        chart.setTitle("Presión Arterial");
        chart.setPrefHeight(300);

        XYChart.Series<String, Number> systolicSeries = new XYChart.Series<>();
        systolicSeries.setName("Sistólica");

        XYChart.Series<String, Number> diastolicSeries = new XYChart.Series<>();
        diastolicSeries.setName("Diastólica");

        for (Metric m : data) {
            PressureMetric p = (PressureMetric) m;
            systolicSeries.getData().add(new XYChart.Data<>(p.getDate(), p.getSystolic()));
            diastolicSeries.getData().add(new XYChart.Data<>(p.getDate(), p.getDiastolic()));
        }

        chart.getData().addAll(systolicSeries, diastolicSeries);
        bloodPressureChartContainer.getChildren().add(chart);
    }

    private void createHeartRateChart(ObservableList<Metric> data) {
        heartRateChartContainer.getChildren().clear();
        if (data.isEmpty()) {
            heartRateChartContainer.getChildren().add(new Label("No hay datos de frecuencia cardíaca"));
            return;
        }

        BarChart<String, Number> chart = new BarChart<>(new javafx.scene.chart.CategoryAxis(), new javafx.scene.chart.NumberAxis());
        chart.setTitle("Frecuencia Cardíaca");
        chart.setPrefHeight(300);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("bpm");

        for (Metric m : data) {
            HeartRateMetric h = (HeartRateMetric) m;
            series.getData().add(new XYChart.Data<>(h.getDate(), h.getHeartRate()));
        }

        chart.getData().add(series);
        heartRateChartContainer.getChildren().add(chart);
    }

    private void createWeightChart(ObservableList<Metric> data) {
        weightChartContainer.getChildren().clear();
        if (data.isEmpty()) {
            weightChartContainer.getChildren().add(new Label("No hay datos de peso/IMC"));
            return;
        }

        BarChart<String, Number> chart = new BarChart<>(new javafx.scene.chart.CategoryAxis(), new javafx.scene.chart.NumberAxis());
        chart.setTitle("Peso Corporal");
        chart.setPrefHeight(300);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Peso (kg)");

        for (Metric m : data) {
            WeightMetric w = (WeightMetric) m;
            series.getData().add(new XYChart.Data<>(w.getDate(), w.getWeight()));
        }

        chart.getData().add(series);
        weightChartContainer.getChildren().add(chart);
    }

    @FXML
    public void showDashboard(ActionEvent event) {
        hideAllSections();
        dashboardSection.setVisible(true);
        updateDashboard();
    }

    @FXML
    public void showPatientsList(ActionEvent event) {
        hideAllSections();
        patientsListSection.setVisible(true);
        patientsListView.setItems(myPatients);
    }

    @FXML
    public void showPendingRequests(ActionEvent event) {
        hideAllSections();
        pendingRequestsSection.setVisible(true);
        loadPendingRequests();
    }

    @FXML
    public void showPatientMetrics(ActionEvent event) {
        Patient selected = patientsListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showPatientMetrics(selected);
        } else if (patientSelector.getValue() != null) {
            showPatientMetrics(patientSelector.getValue());
        } else {
            showErrorAlert("Selección requerida", "Por favor selecciona un paciente de la lista.");
        }
    }

    @FXML
    public void showReports(ActionEvent event) {
        showInfoAlert("Reportes", "Función de generación de reportes en desarrollo");
    }

    @FXML
    public void backToPatientsList(ActionEvent event) {
        hideAllSections();
        patientsListSection.setVisible(true);
    }

    @FXML
    public void generateClinicalReport(ActionEvent event) {
        Patient selected = patientSelector.getValue();
        if (selected != null) {
            showInfoAlert("Reporte Clínico", "Generando reporte para " + selected.getName());
        } else {
            showErrorAlert("Error", "Por favor seleccione un paciente primero");
        }
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
        dashboardSection.setVisible(false);
        patientsListSection.setVisible(false);
        pendingRequestsSection.setVisible(false);
        patientMetricsSection.setVisible(false);
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
