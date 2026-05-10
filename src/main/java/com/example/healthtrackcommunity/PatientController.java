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
import java.time.format.DateTimeFormatter;

public class PatientController {

    @FXML public Label patientNameLabel;
    @FXML public Label assignedDoctorLabel;
    @FXML public Button requestDoctorBtn;
    @FXML public Label requestStatusLabel;
    @FXML public VBox dashboardSection;
    @FXML public VBox noDoctorWarning;
    @FXML public Label healthAlertLabel;
    @FXML public TableView<Metric> recentMetricsTable;
    @FXML public Label nextReminderLabel;
    @FXML public VBox registerMetricsSection;
    @FXML public TextField systolicField;
    @FXML public TextField diastolicField;
    @FXML public TextField glucoseField;
    @FXML public TextField weightField;
    @FXML public TextField heightField;
    @FXML public TextField heartRateField;
    @FXML public DatePicker metricDatePicker;
    @FXML public VBox historySection;
    @FXML public TableView<PressureMetric> historyPressureTable;
    @FXML public TableView<GlucoseMetric> historyGlucoseTable;
    @FXML public TableView<HeartRateMetric> historyHeartRateTable;
    @FXML public TableView<WeightMetric> historyWeightTable;
    @FXML public VBox chartsSection;
    @FXML public VBox patientGlucoseChart;
    @FXML public VBox patientPressureChart;
    @FXML public VBox patientHeartRateChart;
    @FXML public VBox patientWeightChart;
    @FXML public VBox recommendationsSection;
    @FXML public ListView<String> recommendationsListView;
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
        metricDatePicker.setValue(LocalDate.now());
        setupTableColumns();
    }

    private void setupTableColumns() {
        recentMetricsTable.getColumns().clear();
        TableColumn<Metric, String> typeCol = new TableColumn<>("Métrica");
        TableColumn<Metric, String> valueCol = new TableColumn<>("Valor");
        TableColumn<Metric, String> dateCol = new TableColumn<>("Fecha");
        recentMetricsTable.getColumns().addAll(typeCol, valueCol, dateCol);
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
        ObservableList<Metric> allMetrics = FXCollections.observableArrayList();
        allMetrics.addAll(glucoseList);
        allMetrics.addAll(pressureList);
        allMetrics.addAll(heartRateList);
        allMetrics.addAll(weightList);

        allMetrics.sort((a, b) -> b.getDateObj().compareTo(a.getDateObj()));

        ObservableList<Metric> recent = FXCollections.observableArrayList();
        for (int i = 0; i < Math.min(10, allMetrics.size()); i++) {
            recent.add(allMetrics.get(i));
        }

        updateMetricsTable(recent);
    }

    private void updateMetricsTable(ObservableList<Metric> metrics) {
        recentMetricsTable.getItems().clear();
        for (Metric m : metrics) {
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

            Metric displayMetric = new GlucoseMetric(logged.getId(), 0);
            recentMetricsTable.getItems().add(displayMetric);
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
    public void showRegisterMetrics(ActionEvent event) {
        hideAllSections();
        registerMetricsSection.setVisible(true);
    }

    @FXML
    public void showHistory(ActionEvent event) {
        hideAllSections();
        historySection.setVisible(true);
        loadHistoryTables();
    }

    @FXML
    public void showCharts(ActionEvent event) {
        hideAllSections();
        chartsSection.setVisible(true);
        loadCharts();
    }

    @FXML
    public void showRecommendations(ActionEvent event) {
        hideAllSections();
        recommendationsSection.setVisible(true);
        loadRecommendations();
    }

    @FXML
    public void saveMetrics(ActionEvent event) {
        try {
            if (!systolicField.getText().isEmpty() && !diastolicField.getText().isEmpty()) {
                int sys = Integer.parseInt(systolicField.getText());
                int dia = Integer.parseInt(diastolicField.getText());
                PressureMetric pressure = new PressureMetric(logged.getId(), sys, dia);
                pressure.setDate(metricDatePicker.getValue().toString());
                pressureDAO.create(pressure);
            }

            if (!glucoseField.getText().isEmpty()) {
                int glu = Integer.parseInt(glucoseField.getText());
                GlucoseMetric glucose = new GlucoseMetric(logged.getId(), glu);
                glucose.setDate(metricDatePicker.getValue().toString());
                glucoseDAO.create(glucose);
            }

            if (!heartRateField.getText().isEmpty()) {
                int hr = Integer.parseInt(heartRateField.getText());
                HeartRateMetric heartRate = new HeartRateMetric(logged.getId(), hr);
                heartRate.setDate(metricDatePicker.getValue().toString());
                heartRateDAO.create(heartRate);
            }

            if (!weightField.getText().isEmpty() && !heightField.getText().isEmpty()) {
                int weight = Integer.parseInt(weightField.getText());
                int height = Integer.parseInt(heightField.getText());
                WeightMetric wm = new WeightMetric(logged.getId(), height, weight);
                wm.setDate(metricDatePicker.getValue().toString());
                weightDAO.create(wm);
            }

            showInfoAlert("Éxito", "Métricas guardadas correctamente");
            clearForm();
            checkHealthAlerts();

        } catch (NumberFormatException e) {
            showErrorAlert("Error", "Por favor ingresa valores numéricos válidos");
        }
    }

    @FXML
    public void clearForm(ActionEvent event) {
        clearForm();
    }

    private void clearForm() {
        systolicField.clear();
        diastolicField.clear();
        glucoseField.clear();
        weightField.clear();
        heightField.clear();
        heartRateField.clear();
        metricDatePicker.setValue(LocalDate.now());
    }

    @FXML
    public void exportHistory(ActionEvent event) {
        showInfoAlert("Exportar", "Función de exportación en desarrollo");
    }

    @FXML
    public void exportChart(ActionEvent event) {
        showInfoAlert("Exportar", "Función de exportación en desarrollo");
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
        registerMetricsSection.setVisible(false);
        historySection.setVisible(false);
        chartsSection.setVisible(false);
        recommendationsSection.setVisible(false);
    }

    private void loadHistoryTables() {
        historyGlucoseTable.getItems().setAll((GlucoseMetric) glucoseList);
        historyPressureTable.getItems().setAll((PressureMetric) pressureList);
        historyHeartRateTable.getItems().setAll((HeartRateMetric) heartRateList);
        historyWeightTable.getItems().setAll((WeightMetric) weightList);
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

    private void loadRecommendations() {
        recommendationsListView.getItems().clear();

        if (!glucoseList.isEmpty()) {
            GlucoseMetric last = (GlucoseMetric) glucoseList.get(glucoseList.size() - 1);
            if (last.getGlucose() > 140) {
                recommendationsListView.getItems().add("🔴 Glucosa elevada: Reduce el consumo de azúcares y carbohidratos refinados. Realiza actividad física regularmente.");
            } else if (last.getGlucose() < 70) {
                recommendationsListView.getItems().add("🟡 Glucosa baja: Consume una fuente de carbohidratos de absorción rápida. Mantén horarios regulares de comida.");
            } else {
                recommendationsListView.getItems().add("✅ Niveles de glucosa normales: ¡Continúa con tus hábitos saludables!");
            }
        }

        if (!pressureList.isEmpty()) {
            PressureMetric last = (PressureMetric) pressureList.get(pressureList.size() - 1);
            if (last.getSystolic() > 140 || last.getDiastolic() > 90) {
                recommendationsListView.getItems().add("❤️ Presión arterial elevada: Reduce el consumo de sodio, aumenta la ingesta de potasio y realiza ejercicio aeróbico.");
            } else {
                recommendationsListView.getItems().add("💚 Presión arterial saludable: Mantén una dieta balanceada baja en sodio.");
            }
        }

        if (!weightList.isEmpty()) {
            WeightMetric last = (WeightMetric) weightList.get(weightList.size() - 1);
            if (last.getBmi() > 30) {
                recommendationsListView.getItems().add("⚖️ Sobrepeso/Obesidad: Considera consultar a un nutriólogo. Aumenta la actividad física diaria.");
            } else if (last.getBmi() < 18.5) {
                recommendationsListView.getItems().add("⚖️ Bajo peso: Consulta a un especialista para evaluar tu alimentación.");
            } else {
                recommendationsListView.getItems().add("👍 IMC saludable: ¡Sigue manteniendo un estilo de vida activo y alimentación balanceada!");
            }
        }

        if (recommendationsListView.getItems().isEmpty()) {
            recommendationsListView.getItems().add("📋 Registra más métricas para recibir recomendaciones personalizadas.");
        }

        recommendationsListView.getItems().add("");
        recommendationsListView.getItems().add("🌡️ Recomendaciones generales:");
        recommendationsListView.getItems().add("• Realiza al menos 30 minutos de actividad física diaria");
        recommendationsListView.getItems().add("• Mantén una hidratación adecuada (2-3 litros de agua al día)");
        recommendationsListView.getItems().add("• Duerme entre 7-8 horas diarias");
        recommendationsListView.getItems().add("• Realiza chequeos médicos periódicos");
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
