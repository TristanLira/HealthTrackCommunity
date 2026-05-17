package com.example.healthtrackcommunity;

import com.example.healthtrackcommunity.controls.*;
import com.example.healthtrackcommunity.models.*;
import com.google.cloud.firestore.pipeline.stages.Database;
import com.google.firebase.database.*;
import config.*;
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
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;

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

    ObservableList<Metric> recent;


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

        recent = FXCollections.observableArrayList();
        getRecentMetrics(); //Obtiene solo las mediciones recientes. No se usa un DAO porque solo es una query, se hace directo.
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
        loadMetricDisplays();
        loadRecentDisplays();
    }

    /***************MOSTRAR SECCIONES***********/

    @FXML
    public void showDashboard(ActionEvent event) {
        hideAllSections();
        dashboardSection.setVisible(true);
        dashboardSection.setManaged(true);
    }

    @FXML
    public void showHistory(ActionEvent event) {
        hideAllSections();
        historySection.setVisible(true);
        historySection.setManaged(true);

        Platform.runLater(() -> {
            historySection.requestLayout();
            historySection.layout();
        });
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

    /********************************** agregar displays a historySection ******************************************/

    private void loadMetricDisplays() {
        loadDisplay(pressure, historyPressureContainer, PressureMetric.class);
        loadDisplay(heartRate, historyHeartRateContainer, HeartRateMetric.class);
        loadDisplay(glucose, historyGlucoseContainer, GlucoseMetric.class);
        loadDisplay(weight, historyWeightContainer, WeightMetric.class);
    }

    private void loadDisplay(ObservableList<Metric> list, VBox container, Class<? extends Metric> metricClass) {
        list.addListener((ListChangeListener<? super Metric>) change -> {

            while (change.next()) {
                if (change.wasAdded()) {

                    for (Metric i: change.getAddedSubList()) {
                        if (i.getClass() != metricClass) continue; //no debería haber otro tipo de métricas en esta lista, pero por si acaso
                        MetricDisplay display = getDisplay(i);
                        display.hideTitle();
                        Platform.runLater(() -> container.getChildren().addFirst(display));
                        /*Ya que en la base de datos las mediciones se guardan en orden de registro, al leerlas se obtienen primero las más
                         * antiguas. Guardando cada medición recibida de la base de datos al inicio, se terminan mostrando ordenadas en la GUI*/
                    }

                }
            }
        });
    }


    /********************************** mostrar las mediciones recientes ******************************************/

    private void getRecentMetrics() {
        DatabaseReference ref = FirebaseConnection.getDB().getReference("metrics");

        metricQuery(ref.child("pressure"), PressureMetric.class);
        metricQuery(ref.child("heartRate"), HeartRateMetric.class);
        metricQuery(ref.child("glucose"), GlucoseMetric.class);
        metricQuery(ref.child("weight"), WeightMetric.class);
    }

    private void metricQuery(DatabaseReference ref, Class <? extends Metric> metricClass) {
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Metric m = dataSnapshot.getValue(metricClass);
                if (isInLastWeek(m.getDateObj())) {
                    recent.add(m);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Metric m = dataSnapshot.getValue(metricClass);
                if (recent.contains(m)) {
                    recent.remove(m);
                    recent.add(m);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Metric m = dataSnapshot.getValue(metricClass);
                recent.remove(m);
            }

            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        });
    }


    private boolean isInLastWeek(LocalDate date) {
        LocalDate lastWeek = LocalDate.now().minusWeeks(1);
        return date.isAfter(lastWeek) || date.isEqual(lastWeek);
    }

    private void loadRecentDisplays() {
        recent.addListener((ListChangeListener<? super Metric>) change -> {

            while (change.next()) {
                if (change.wasAdded()) {

                    for (Metric i: change.getAddedSubList()) {
                        Platform.runLater(() ->
                                recentMetricsContainer.getChildren().addFirst(getDisplay(i)));
                    }

                }
            }
        });
    }

    private MetricDisplay getDisplay(Metric m) {
        MetricDisplay display;

        if (m instanceof PressureMetric) {
            display = new PressureDisplay((PressureMetric) m);
        }
        else if (m instanceof HeartRateMetric) {
            display = new HeartRateDisplay((HeartRateMetric) m);
        }
        else if (m instanceof GlucoseMetric) {
            display = new GlucoseDisplay((GlucoseMetric) m);
        }
        else if (m instanceof WeightMetric) {
            display = new WeightDisplay((WeightMetric) m);
        } else {
            display = new MetricDisplay(m);
        }

        return display;
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
