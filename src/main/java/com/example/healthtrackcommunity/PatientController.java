package com.example.healthtrackcommunity;

import com.example.healthtrackcommunity.models.*;
import config.MetricDAO;
import config.MonitoringRequestDAO;
import config.PatientDAO;
import config.DoctorDAO;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class PatientController {

    public ScrollPane mainScrollPane;
    public StackPane mainContent;
    public Label patientNameLabel;
    public Label assignedDoctorLabel;

    //sección de dashboard
    public VBox dashboardSection;
    public VBox noDoctorWarning;
    public Label healthAlertLabel;
    public VBox recentMetricsContainer;
    public Label nextReminderLabel;

    //historial de mediciones
    public VBox historySection;
    public VBox historyPressureContainer;
    public VBox historyGlucoseContainer;
    public VBox historyHeartRateContainer;
    public VBox historyWeightContainer;

    //sección de gráficos
    public VBox chartsSection;
    public VBox patientGlucoseChart;
    public VBox patientPressureChart;
    public VBox patientHeartRateChart;
    public VBox patientWeightChart;

    //seguimiento médico
    public VBox doctorMonitoringSection;
    public VBox doctorNotesContainer;

    /*formularios de registro de métricas*/
    public VBox registerMetricsSection;
    public ComboBox<String> metricTypeComboBox;
    public StackPane metricFormsStackPane;

    public VBox heartRateMetricForm;
    public TextField heartRateField;
    public Button saveHeartRateBtn;

    public VBox pressureMetricForm;
    public TextField pressureSystolicField;
    public TextField pressureDiastolicField;
    public Button savePressureBtn;

    public VBox glucoseMetricForm;
    public TextField glucoseField;
    public Button saveGlucoseBtn;

    public VBox weightMetricForm;
    public TextField weightField;
    public TextField heightField;
    public Button saveWeightBtn;

    /*formulario de solicitud de seguimiento*/
    public VBox monitoringRequestForm;
    public ComboBox<Doctor> doctorsComboBox;
    public Button sendMonitoringRequestBtn;

    //TABLAS PARA MOSTRAR LOS DATOS

    public TableView<PressureMetric> pressureTableView;
    public TableColumn<PressureMetric, String> pressureDateColumn;
    public TableColumn<PressureMetric, String> pressureTimeColumn;
    public TableColumn<PressureMetric, Integer> pressureSystolicColumn;
    public TableColumn<PressureMetric, Integer> pressureDiastolicColumn;

    public TableView<GlucoseMetric> glucoseTableView;
    public TableColumn<GlucoseMetric, String> glucoseDateColumn;
    public TableColumn<GlucoseMetric, String> glucoseTimeColumn;
    public TableColumn<GlucoseMetric, Integer> glucoseValueColumn;

    public TableView<HeartRateMetric> heartRateTableView;
    public TableColumn<HeartRateMetric, String> heartRateDateColumn;
    public TableColumn<HeartRateMetric, String> heartRateTimeColumn;
    public TableColumn<HeartRateMetric, Integer> heartRateValueColumn;

    public TableView<WeightMetric> weightTableView;
    public TableColumn<WeightMetric, String> weightDateColumn;
    public TableColumn<WeightMetric, String> weightTimeColumn;
    public TableColumn<WeightMetric, Integer> weightValueColumn;
    public TableColumn<WeightMetric, Double> imcColumn;

    //información de pacientes y doctores
    private PatientDAO patientDAO;
    private DoctorDAO doctorDAO;
    private Patient logged;
    private Doctor monitoring;

    //DAOs
    private MetricDAO heartRateDAO;
    private MetricDAO pressureDAO;
    private MetricDAO glucoseDAO;
    private MetricDAO weightDAO;
    private MonitoringRequestDAO requestDAO;

    //listas
    ObservableList<Patient> patients;
    ObservableList<Doctor> doctors;
    ObservableList<Metric> heartRate;
    ObservableList<Metric> pressure;
    ObservableList<Metric> glucose;
    ObservableList<Metric> weight;

    public void initialize() {
        hideAllSections();
        dashboardSection.setManaged(true);
        dashboardSection.setVisible(true);
    }

    private void initMetricDAOs() {
        heartRateDAO = new MetricDAO(logged, MetricDAO.HEART_RATE);
        pressureDAO = new MetricDAO(logged, MetricDAO.PRESSURE);
        glucoseDAO = new MetricDAO(logged, MetricDAO.GLUCOSE);
        weightDAO = new MetricDAO(logged, MetricDAO.WEIGHT);
        requestDAO = new MonitoringRequestDAO(logged);

        heartRate = heartRateDAO.getAll();
        pressure = pressureDAO.getAll();
        glucose = glucoseDAO.getAll();
        weight = weightDAO.getAll();
    }

    public void setLoggedUser(PatientDAO dao, DoctorDAO doctorDAO, Patient logged) {
        this.logged = logged;

        this.patientDAO = dao;
        this.doctorDAO = doctorDAO;
        patients = patientDAO.getAll();
        doctors = doctorDAO.getAll();

        initMetricDAOs();
        initMetricTypeCombobox();
        initDoctorsComboBox();

        initHistoryTables();
    }

    /***************MOSTRAR SECCIONES***********/

    @FXML
    public void showDashboard(ActionEvent event) {
        hideAllSections();
        dashboardSection.setVisible(true);
        dashboardSection.setManaged(true);
    }

    public void showMetricRegister(ActionEvent actionEvent) {
        hideAllSections();
        registerMetricsSection.setVisible(true);
        registerMetricsSection.setManaged(true);
    }

    @FXML
    public void showHistory(ActionEvent event) {
        hideAllSections();
        historySection.setVisible(true);
        historySection.setManaged(true);
    }

    @FXML
    public void showCharts(ActionEvent event) {
        hideAllSections();
        chartsSection.setVisible(true);
        chartsSection.setManaged(true);
    }

    public void showDoctorMonitoring(ActionEvent actionEvent) {
        hideAllSections();
        doctorMonitoringSection.setManaged(true);
        doctorMonitoringSection.setVisible(true);
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
            i.setVisible(false);
            i.setManaged(false);
        }
    }

    /*************************Registro de metricas******************************/

    private void initMetricTypeCombobox() {
        final String heartRateStr = "Frecuencia cardíaca";
        final String pressureStr = "Presión arterial";
        final String glucoseStr = "Glucosa";
        final String weightStr = "Indice de masa corporal";

        ObservableList<String> comboBoxList = FXCollections.observableArrayList(pressureStr, heartRateStr, glucoseStr, weightStr);

        metricTypeComboBox.setItems(comboBoxList);

        metricTypeComboBox.setOnAction(event -> {

            switch (metricTypeComboBox.getValue()) {
                case heartRateStr:
                    showMetricForm(heartRateMetricForm);
                    break;

                case pressureStr:
                    showMetricForm(pressureMetricForm);
                    break;

                case glucoseStr:
                    showMetricForm(glucoseMetricForm);
                    break;

                case weightStr:
                    showMetricForm(weightMetricForm);
                    break;
            }

        });

        showMetricForm(pressureMetricForm);
        metricTypeComboBox.setValue(pressureStr);
    }

    private void showMetricForm(Node n) {
        for (Node i: metricFormsStackPane.getChildren()) {
            i.setManaged(false);
            i.setVisible(false);
        }
        n.setManaged(true);
        n.setVisible(true);
    }


    /*FRECUENCIA CARDIACA*/
    public void saveHeartRate(ActionEvent actionEvent) {
        HeartRateMetric h = getHeartRateMetric();
        if (h == null) {
            errorAlert("Datos inválidos", "Los datos ingresados no son validos. Por favor ingrese solo números.");
            return;
        }
        heartRateDAO.create(h);
    }

    private HeartRateMetric getHeartRateMetric() {
        int heartRate;

        try {
            heartRate = Integer.parseInt(heartRateField.getText().strip());
        } catch (Exception e) {
            return null;
        }

        heartRateField.clear();
        return new HeartRateMetric(logged.getId(), heartRate);
    }


    /*PRESIÓN ARTERIAL*/
    public void savePressure(ActionEvent actionEvent) {
        PressureMetric p = getPressureMetric();
        if (p == null) {
            errorAlert("Datos inválidos", "Los datos ingresados no son validos. Por favor ingrese solo números.");
            return;
        }
        pressureDAO.create(p);
    }

    private PressureMetric getPressureMetric() {
        int systolic, diastolic;

        try {
            systolic = Integer.parseInt(pressureSystolicField.getText().strip());
            diastolic = Integer.parseInt(pressureDiastolicField.getText().strip());
        } catch (Exception e) {
            return null;
        }

        pressureSystolicField.clear();
        pressureDiastolicField.clear();
        return new PressureMetric(logged.getId(), systolic, diastolic);
    }


    /*GLUCOSA*/
    public void saveGlucose(ActionEvent actionEvent) {
        GlucoseMetric g = getGlucoseMetric();
        if (g == null) {
            errorAlert("Datos inválidos", "Los datos ingresados no son validos. Por favor ingrese solo números.");
            return;
        }
        glucoseDAO.create(g);
    }

    private GlucoseMetric getGlucoseMetric() {
        int glucose;

        try {
            glucose = Integer.parseInt(glucoseField.getText().strip());
        } catch (Exception e) {
            return null;
        }

        glucoseField.clear();
        return new GlucoseMetric(logged.getId(), glucose);
    }


    /*PESO/IMC*/
    public void saveWeight(ActionEvent actionEvent) {
        WeightMetric w = getWeightMetric();
        if (w == null) {
            errorAlert("Datos inválidos", "Los datos ingresados no son validos. Por favor ingrese solo números.");
            return;
        }
        weightDAO.create(w);
    }

    private WeightMetric getWeightMetric() {
        int weight, height;

        try {
            weight = Integer.parseInt(weightField.getText().strip());
            height = Integer.parseInt(heightField.getText().strip());
        } catch (Exception e) {
            return null;
        }

        weightField.clear();
        heightField.clear();
        return new WeightMetric(logged.getId(), height, weight);
    }

    /********************************** solicitud de seguimiento médico ******************************************/

    private void initDoctorsComboBox() {
        doctorsComboBox.setItems(doctors);

        if (!logged.getDoctorId().isEmpty()) {
            monitoring = doctorDAO.get(logged.getDoctorId());

            if (monitoring == null) return;

            //oculta el formulario cuando un usuario ya tiene un doctor asignado
            monitoringRequestForm.setVisible(false);
            monitoringRequestForm.setManaged(false);
        }
    }

    public void sendMonitoringRequest(ActionEvent actionEvent) {
        Doctor d = doctorsComboBox.getValue();
        doctorsComboBox.setValue(null);

        if (d == null) {
            errorAlert("Seleccione médico", "No fue posible mandar la solicitud, por favor seleccione un médico.");
            return;
        }

        MonitoringRequest m = new MonitoringRequest(logged.getId(), d.getId());

        //corre las alertas con runLater porque los callbacks son llamados por firebase en un hilo diferente
        requestDAO.create(m,
                () -> Platform.runLater(
                        () -> infoAlert("Solicitud enviada", "Un solicitud de seguimiento médico fue enviada a " + d + ".")),
                () -> Platform.runLater(
                        () -> errorAlert("Solicitud no enviada", "La solicitud no fue enviada. Por favor inténtelo de nuevo."))
        );
    }

    /********************************** mostrar historial de mediciones ******************************************/

    private void initHistoryTables() {

        //presion

        pressureDateColumn.setCellValueFactory(
                cell -> new SimpleStringProperty(
                        cell.getValue().getDate()
                ));

        pressureTimeColumn.setCellValueFactory(
                cell -> new SimpleStringProperty(
                        cell.getValue().getTime()
                ));

        pressureSystolicColumn.setCellValueFactory(
                cell -> new SimpleIntegerProperty(
                        cell.getValue().getSystolic()
                ).asObject()
        );

        pressureDiastolicColumn.setCellValueFactory(
                cell -> new SimpleIntegerProperty(
                        cell.getValue().getDiastolic()
                ).asObject()
        );


        //glucosa

        glucoseDateColumn.setCellValueFactory(
                cell -> new SimpleStringProperty(
                        cell.getValue().getDate()
                ));

        glucoseTimeColumn.setCellValueFactory(
                cell -> new SimpleStringProperty(
                        cell.getValue().getTime()
                ));

        glucoseValueColumn.setCellValueFactory(
                cell -> new SimpleIntegerProperty(
                        cell.getValue().getGlucose()
                ).asObject()
        );


        //Frecuencia cardiaca

        heartRateDateColumn.setCellValueFactory(
                cell -> new SimpleStringProperty(
                        cell.getValue().getDate()
                ));

        heartRateTimeColumn.setCellValueFactory(
                cell -> new SimpleStringProperty(
                        cell.getValue().getTime()
                ));

        heartRateValueColumn.setCellValueFactory(
                cell -> new SimpleIntegerProperty(
                        cell.getValue().getHeartRate()
                ).asObject()
        );


        //peso

        weightDateColumn.setCellValueFactory(
                cell -> new SimpleStringProperty(
                        cell.getValue().getDate()
                ));

        weightTimeColumn.setCellValueFactory(
                cell -> new SimpleStringProperty(
                        cell.getValue().getTime()
                ));

        weightValueColumn.setCellValueFactory(
                cell -> new SimpleIntegerProperty(
                        cell.getValue().getWeight()
                ).asObject()
        );

        imcColumn.setCellValueFactory(
                cell -> new SimpleDoubleProperty(
                        cell.getValue().getBmi()
                ).asObject()
        );

        bindHistoryTables();
    }

    @SuppressWarnings("unchecked")
    private void bindHistoryTables() {

        pressureTableView.setItems(
                (ObservableList<PressureMetric>)
                        (ObservableList<?>) pressure
        );

        glucoseTableView.setItems(
                (ObservableList<GlucoseMetric>)
                        (ObservableList<?>) glucose
        );

        heartRateTableView.setItems(
                (ObservableList<HeartRateMetric>)
                        (ObservableList<?>) heartRate
        );

        weightTableView.setItems(
                (ObservableList<WeightMetric>)
                        (ObservableList<?>) weight
        );
    }


    /********ALERTAS********/

    private void infoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.show();
    }

    private void errorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.show();
    }

}
