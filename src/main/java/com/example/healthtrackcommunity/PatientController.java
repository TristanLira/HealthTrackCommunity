package com.example.healthtrackcommunity;

import com.example.healthtrackcommunity.models.*;
import config.DoctorDAO;
import config.MetricDAO;
import config.PatientDAO;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PatientController {

    /*formularios de registro de métricas*/

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

    //listas
    ObservableList<Patient> patients;
    ObservableList<Doctor> doctors;
    ObservableList<Metric> heartRate;
    ObservableList<Metric> pressure;
    ObservableList<Metric> glucose;
    ObservableList<Metric> weight;

    public void initialize() {
    }

    private void initMetricDAOs() {
        heartRateDAO = new MetricDAO(logged, MetricDAO.HEART_RATE);
        pressureDAO = new MetricDAO(logged, MetricDAO.PRESSURE);
        glucoseDAO = new MetricDAO(logged, MetricDAO.GLUCOSE);
        weightDAO = new MetricDAO(logged, MetricDAO.WEIGHT);

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
    }

    /********************************** ALERTAS ******************************************/

    private void errorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.show();
    }

    private void infoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.show();
    }
}
