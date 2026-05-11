package com.example.healthtrackcommunity;

import com.example.healthtrackcommunity.models.*;
import config.MetricDAO;
import config.PatientDAO;
import config.DoctorDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;

public class PatientController {

    @FXML public Label patientNameLabel;
    @FXML public Label assignedDoctorLabel;
    @FXML public Button requestDoctorBtn;
    @FXML public Label requestStatusLabel;
    @FXML public VBox dashboardSection;
    @FXML public VBox noDoctorWarning;
    @FXML public Label healthAlertLabel;
    @FXML public VBox recentMetricsContainer;
    @FXML public Label nextReminderLabel;
    @FXML public VBox historySection;
    @FXML public VBox historyPressureContainer;
    @FXML public VBox historyGlucoseContainer;
    @FXML public VBox historyHeartRateContainer;
    @FXML public VBox historyWeightContainer;
    @FXML public VBox chartsSection;
    @FXML public VBox patientGlucoseChart;
    @FXML public VBox patientPressureChart;
    @FXML public VBox patientHeartRateChart;
    @FXML public VBox patientWeightChart;
    @FXML public ScrollPane mainScrollPane;
    @FXML public StackPane mainContent;

    private PatientDAO patientDAO;
    private Patient logged;
    private DoctorDAO doctorDAO;
    private MetricDAO heartRateDAO;
    private MetricDAO pressureDAO;
    private MetricDAO glucoseDAO;
    private MetricDAO weightDAO;
    private ObservableList<Metric> heartRateList;
    private ObservableList<Metric> pressureList;
    private ObservableList<Metric> glucoseList;
    private ObservableList<Metric> weightList;
    private String requestStatus = "";

    public void initialize() {
    }

    public void setLoggedUser(PatientDAO dao, Patient logged) {
        this.logged = logged;
        this.patientDAO = dao;
        this.doctorDAO = new DoctorDAO();

        patientNameLabel.setText(logged.getName());
        checkDoctorAssignment();
        initDAOs();
        loadRecentMetrics();
        checkHealthAlerts();
    }

    private void checkDoctorAssignment() {
        if (logged.getDoctorId() == null || logged.getDoctorId().isEmpty()) {
            assignedDoctorLabel.setText("Ninguno");
            requestDoctorBtn.setVisible(true);
            requestDoctorBtn.setManaged(true);
            noDoctorWarning.setVisible(true);
            noDoctorWarning.setManaged(true);

            if (requestStatus.equals("pendiente")) {
                requestStatusLabel.setText("Solicitud enviada - Esperando respuesta");
                requestStatusLabel.setVisible(true);
                requestStatusLabel.setManaged(true);
                requestDoctorBtn.setDisable(true);
            }
        } else {
            Doctor doctor = doctorDAO.get(logged.getDoctorId());
            if (doctor != null) {
                assignedDoctorLabel.setText("Dr. " + doctor.getName() + " (" + doctor.getSpecialization() + ")");
            }
            requestDoctorBtn.setVisible(false);
            requestDoctorBtn.setManaged(false);
            noDoctorWarning.setVisible(false);
            noDoctorWarning.setManaged(false);
            requestStatusLabel.setVisible(false);
        }
    }

    private void initDAOs() {
        heartRateDAO = new MetricDAO(logged, MetricDAO.HEART_RATE);
        pressureDAO = new MetricDAO(logged, MetricDAO.PRESSURE);
        glucoseDAO = new MetricDAO(logged, MetricDAO.GLUCOSE);
        weightDAO = new MetricDAO(logged, MetricDAO.WEIGHT);

        heartRateList = heartRateDAO.getAll();
        pressureList = pressureDAO.getAll();
        glucoseList = glucoseDAO.getAll();
        weightList = weightDAO.getAll();
    }

    private void loadRecentMetrics() {
        recentMetricsContainer.getChildren().clear();

        ObservableList<Metric> allMetrics = FXCollections.observableArrayList();
        allMetrics.addAll(glucoseList);
        allMetrics.addAll(pressureList);
        allMetrics.addAll(heartRateList);
        allMetrics.addAll(weightList);

        allMetrics.sort((a, b) -> b.getDateObj().compareTo(a.getDateObj()));

        int count = 0;
        for (Metric m : allMetrics) {
            if (count >= 10) break;

            String type = "";
            String value = "";

            if (m instanceof GlucoseMetric) {
                type = "Glucosa";
                value = ((GlucoseMetric) m).getGlucose() + " mg/dL";
            } else if (m instanceof PressureMetric) {
                type = "Presión";
                PressureMetric p = (PressureMetric) m;
                value = p.getSystolic() + "/" + p.getDiastolic() + " mmHg";
            } else if (m instanceof HeartRateMetric) {
                type = "Frecuencia Cardíaca";
                value = ((HeartRateMetric) m).getHeartRate() + " bpm";
            } else if (m instanceof WeightMetric) {
                type = "Peso/IMC";
                WeightMetric w = (WeightMetric) m;
                value = w.getWeight() + " kg | IMC: " + String.format("%.1f", w.getBmi());
            }

            Label metricLabel = new Label(type + ": " + value + " - " + m.getDate());
            metricLabel.setStyle("-fx-padding: 8; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");
            recentMetricsContainer.getChildren().add(metricLabel);
            count++;
        }

        if (allMetrics.isEmpty()) {
            Label emptyLabel = new Label("No hay métricas registradas");
            emptyLabel.setStyle("-fx-padding: 8; -fx-text-fill: #999;");
            recentMetricsContainer.getChildren().add(emptyLabel);
        }
    }

    private void checkHealthAlerts() {
        StringBuilder alerts = new StringBuilder();

        if (!glucoseList.isEmpty()) {
            GlucoseMetric last = (GlucoseMetric) glucoseList.get(glucoseList.size() - 1);
            if (last.getGlucose() > 140) {
                alerts.append("⚠️ Glucosa elevada: ").append(last.getGlucose()).append(" mg/dL\n");
            } else if (last.getGlucose() < 70) {
                alerts.append("⚠️ Glucosa baja: ").append(last.getGlucose()).append(" mg/dL\n");
            }
        }

        if (!pressureList.isEmpty()) {
            PressureMetric last = (PressureMetric) pressureList.get(pressureList.size() - 1);
            if (last.getSystolic() > 140 || last.getDiastolic() > 90) {
                alerts.append("⚠️ Presión arterial elevada: ").append(last.getSystolic()).append("/").append(last.getDiastolic()).append("\n");
            }
        }

        if (alerts.length() == 0) {
            healthAlertLabel.setText("✅ Tus métricas están dentro de rangos normales");
        } else {
            healthAlertLabel.setText(alerts.toString());
        }
    }

    @FXML
    public void requestDoctor(ActionEvent event) {
        requestStatus = "pendiente";
        requestStatusLabel.setText("Solicitud enviada - Esperando respuesta");
        requestStatusLabel.setVisible(true);
        requestStatusLabel.setManaged(true);
        requestDoctorBtn.setDisable(true);

        showInfoAlert("Solicitud enviada", "Se ha enviado tu solicitud. Un médico te contactará pronto.");
    }

    @FXML
    public void showDashboard(ActionEvent event) {
        hideAllSections();
        dashboardSection.setVisible(true);
        loadRecentMetrics();
        checkHealthAlerts();
    }

    @FXML
    public void showHistory(ActionEvent event) {
        hideAllSections();
        historySection.setVisible(true);
        loadHistory();
    }

    @FXML
    public void showCharts(ActionEvent event) {
        hideAllSections();
        chartsSection.setVisible(true);
        loadCharts();
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
        historySection.setVisible(false);
        chartsSection.setVisible(false);
    }

    private void loadHistory() {
        historyPressureContainer.getChildren().clear();
        historyGlucoseContainer.getChildren().clear();
        historyHeartRateContainer.getChildren().clear();
        historyWeightContainer.getChildren().clear();

        for (Metric m : pressureList) {
            PressureMetric p = (PressureMetric) m;
            Label label = new Label(p.getSystolic() + "/" + p.getDiastolic() + " mmHg - " + p.getDate());
            label.setStyle("-fx-padding: 8; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");
            historyPressureContainer.getChildren().add(label);
        }

        for (Metric m : glucoseList) {
            GlucoseMetric g = (GlucoseMetric) m;
            Label label = new Label(g.getGlucose() + " mg/dL - " + g.getDate());
            label.setStyle("-fx-padding: 8; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");
            historyGlucoseContainer.getChildren().add(label);
        }

        for (Metric m : heartRateList) {
            HeartRateMetric h = (HeartRateMetric) m;
            Label label = new Label(h.getHeartRate() + " bpm - " + h.getDate());
            label.setStyle("-fx-padding: 8; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");
            historyHeartRateContainer.getChildren().add(label);
        }

        for (Metric m : weightList) {
            WeightMetric w = (WeightMetric) m;
            Label label = new Label(w.getWeight() + " kg | IMC: " + String.format("%.1f", w.getBmi()) + " - " + w.getDate());
            label.setStyle("-fx-padding: 8; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");
            historyWeightContainer.getChildren().add(label);
        }
    }

    private void loadCharts() {
        createLineChart(patientGlucoseChart, glucoseList, "Glucosa", "mg/dL");
        createPressureChart(patientPressureChart, pressureList);
        createLineChart(patientHeartRateChart, heartRateList, "Frecuencia Cardíaca", "bpm");
        createWeightChart(patientWeightChart, weightList);
    }

    private void createLineChart(VBox container, ObservableList<Metric> data, String title, String unit) {
        container.getChildren().clear();
        if (data.isEmpty()) {
            Label label = new Label("No hay datos suficientes para mostrar el gráfico");
            container.getChildren().add(label);
            return;
        }

        LineChart<String, Number> chart = new LineChart<>(new javafx.scene.chart.CategoryAxis(), new javafx.scene.chart.NumberAxis());
        chart.setTitle(title);
        chart.setPrefHeight(400);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(title);

        for (Metric m : data) {
            double value = 0;
            if (m instanceof GlucoseMetric) value = ((GlucoseMetric) m).getGlucose();
            else if (m instanceof HeartRateMetric) value = ((HeartRateMetric) m).getHeartRate();

            series.getData().add(new XYChart.Data<>(m.getDate(), value));
        }

        chart.getData().add(series);
        container.getChildren().add(chart);
    }

    private void createPressureChart(VBox container, ObservableList<Metric> data) {
        container.getChildren().clear();
        if (data.isEmpty()) {
            Label label = new Label("No hay datos suficientes para mostrar el gráfico");
            container.getChildren().add(label);
            return;
        }

        LineChart<String, Number> chart = new LineChart<>(new javafx.scene.chart.CategoryAxis(), new javafx.scene.chart.NumberAxis());
        chart.setTitle("Presión Arterial");
        chart.setPrefHeight(400);

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
        container.getChildren().add(chart);
    }

    private void createWeightChart(VBox container, ObservableList<Metric> data) {
        container.getChildren().clear();
        if (data.isEmpty()) {
            Label label = new Label("No hay datos suficientes para mostrar el gráfico");
            container.getChildren().add(label);
            return;
        }

        LineChart<String, Number> chart = new LineChart<>(new javafx.scene.chart.CategoryAxis(), new javafx.scene.chart.NumberAxis());
        chart.setTitle("Evolución de Peso");
        chart.setPrefHeight(400);

        XYChart.Series<String, Number> weightSeries = new XYChart.Series<>();
        weightSeries.setName("Peso (kg)");

        XYChart.Series<String, Number> bmiSeries = new XYChart.Series<>();
        bmiSeries.setName("IMC");

        for (Metric m : data) {
            WeightMetric w = (WeightMetric) m;
            weightSeries.getData().add(new XYChart.Data<>(w.getDate(), w.getWeight()));
            bmiSeries.getData().add(new XYChart.Data<>(w.getDate(), w.getBmi()));
        }

        chart.getData().addAll(weightSeries, bmiSeries);
        container.getChildren().add(chart);
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
