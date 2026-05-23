package com.example.healthtrackcommunity;

import com.example.healthtrackcommunity.controls.*;
import com.example.healthtrackcommunity.models.*;
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
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    public TabPane historyTab;
    public VBox historyPressureContainer;
    public VBox historyGlucoseContainer;
    public VBox historyHeartRateContainer;
    public VBox historyWeightContainer;

    //sección de gráficos
    public VBox chartsSection;
    public TabPane chartsTab;
    public VBox patientGlucoseChartContainer;
    public VBox patientPressureChartContainer;
    public VBox patientHeartRateChartContainer;
    public VBox patientWeightChartContainer;

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
    public HBox doctorInfoCard;
    public Label doctorNameLabel;
    public Label requestInfoLabel;

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
    ObservableList<MonitoringRequest> requests;
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
        requests = requestDAO.getAll();

        recent = FXCollections.observableArrayList();
        getRecentMetrics(); //Obtiene solo las mediciones recientes. No se usa un DAO porque solo es una query, se hace directo.
    }

    public void setLoggedUser(PatientDAO dao, DoctorDAO doctorDAO, Patient logged) {
        this.logged = logged;
        patientNameLabel.setText(logged.getName());

        this.patientDAO = dao;
        this.doctorDAO = doctorDAO;
        patients = patientDAO.getAll();
        doctors = doctorDAO.getAll();

        initMetricDAOs();

        showDoctorInfo();
        updateRequestInfo();
        updatePatientAccount();

        initMetricTypeCombobox();
        initDoctorsComboBox();

        loadMetricDisplays();
        loadRecentDisplays();

        //addMetricsDebug();
    }

    private void addMetricsDebug() {
        List<PressureMetric> pressureMetrics = new ArrayList<>();
        List<GlucoseMetric> glucoseMetrics = new ArrayList<>();
        List<HeartRateMetric> heartRateMetrics = new ArrayList<>();
        List<WeightMetric> weightMetrics = new ArrayList<>();

        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);

            PressureMetric pressureMetric = new PressureMetric(
                    logged.getId(),
                    110 + (int)(Math.random() * 20), // sistólica random
                    70 + (int)(Math.random() * 15)   // diastólica random
            );

            GlucoseMetric glucoseMetric = new GlucoseMetric(
                    logged.getId(),
                    80 + (int)(Math.random() * 60)
            );

            HeartRateMetric heartRateMetric = new HeartRateMetric(
                    logged.getId(),
                    60 + (int)(Math.random() * 41)
            );

            WeightMetric weightMetric = new WeightMetric(
                    logged.getId(),
                    175,
                    65 + (int)(Math.random() * 10) // peso 65–75 kg
            );

            // asignar fecha
            pressureMetric.setDate(day.toString());
            glucoseMetric.setDate(day.toString());
            heartRateMetric.setDate(day.toString());
            weightMetric.setDate(day.toString());

            pressureMetrics.add(pressureMetric);
            glucoseMetrics.add(glucoseMetric);
            heartRateMetrics.add(heartRateMetric);
            weightMetrics.add(weightMetric);
        }

        for (Metric j: pressureMetrics) pressureDAO.create(j);
        for (Metric j: glucoseMetrics) glucoseDAO.create(j);
        for (Metric j: heartRateMetrics) heartRateDAO.create(j);
        for (Metric j: weightMetrics) weightDAO.create(j);
    }

    private void updateRequestInfo() {
        requests.addListener((ListChangeListener<MonitoringRequest>) change -> {
            while(change.next()) {
                Platform.runLater(() -> showDoctorInfo());
            }
        });
    }

    private void showDoctorInfo() {
        System.out.println("Tamaño de la lista de request: " + requests.size());

        boolean noDoctor = logged.getDoctorId() == null || logged.getDoctorId().isEmpty();

        String requestInfo = requests.isEmpty() ? "No tienes una solicitud activa" : "Solicitud enviada a " + doctorDAO.get(requests.getFirst().getDoctorId()).getName();
        requestInfoLabel.setText("(" + requestInfo + ")");

        noDoctorWarning.setVisible(noDoctor);
        noDoctorWarning.setManaged(noDoctor);

        monitoringRequestForm.setVisible(noDoctor);
        monitoringRequestForm.setManaged(noDoctor);

        doctorInfoCard.setVisible(!noDoctor);
        doctorInfoCard.setManaged(!noDoctor);

        if (doctorDAO.get(logged.getDoctorId()) != null) doctorNameLabel.setText(doctorDAO.get(logged.getDoctorId()).toString());
    }

    private void updatePatientAccount() {
        FirebaseConnection.getDB().getReference("patients").child(logged.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Patient p = dataSnapshot.getValue(Patient.class);

                //no se si actualizar el paciente completo cause problemas, pero solo actualiza el id del doctor por si acaso
                logged.setDoctorId(p.getDoctorId());

                //busca y guarda el doctor
                for (Doctor i: doctors) {
                    if (i.getId().equals(logged.getDoctorId())) {
                        monitoring = i;
                        break;
                    }
                }

                Platform.runLater(() -> showDoctorInfo());
            }

            @Override public void onCancelled(DatabaseError databaseError) {}
        });
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
        reloadTab(historyTab);
    }

    @FXML
    public void showCharts(ActionEvent event) {
        hideAllSections();
        chartsSection.setVisible(true);
        chartsSection.setManaged(true);
        reloadTab(chartsTab);
    }

    public void showDoctorMonitoring(ActionEvent actionEvent) {
        hideAllSections();
        doctorMonitoringSection.setManaged(true);
        doctorMonitoringSection.setVisible(true);
        showDoctorInfo();
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

    private void reloadTab(TabPane pane) {
        Tab selected = pane.getSelectionModel().getSelectedItem();
        pane.getSelectionModel().clearSelection();
        pane.getSelectionModel().select(selected);
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

                } else if (change.wasRemoved()) {

                    for (Metric i: change.getRemoved()) {
                        for (Node j: container.getChildren()) {
                            if (!(j instanceof MetricDisplay)) continue;
                            if ( ((MetricDisplay) j).isMetric(i) ) {
                                Platform.runLater(() -> container.getChildren().remove(j));
                                break;
                            }
                        }
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


    /********************************** mostrar las mediciones recientes ******************************************/

    private void getRecentMetrics() {
        DatabaseReference ref = FirebaseConnection.getDB().getReference("metrics");

        metricQuery(ref.child("pressure"), PressureMetric.class);
        metricQuery(ref.child("heartRate"), HeartRateMetric.class);
        metricQuery(ref.child("glucose"), GlucoseMetric.class);
        metricQuery(ref.child("weight"), WeightMetric.class);
    }

    private void metricQuery(DatabaseReference ref, Class <? extends Metric> metricClass) {
        ref.orderByChild("userId").equalTo(logged.getId()).addChildEventListener(new ChildEventListener() {
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
        return date.isAfter(lastWeek);
    }

    private void loadRecentDisplays() {
        recent.addListener((ListChangeListener<? super Metric>) change -> {

            while (change.next()) {
                if (change.wasAdded()) {

                    for (Metric i: change.getAddedSubList()) {
                        Platform.runLater(() ->
                                recentMetricsContainer.getChildren().addFirst(getDisplay(i)));
                    }

                } else if (change.wasRemoved()) {

                    for (Metric i: change.getRemoved()) {
                        for (Node j: recentMetricsContainer.getChildren()) {
                            if (!(j instanceof MetricDisplay)) continue;
                            if ( ((MetricDisplay) j).isMetric(i) ) {
                                Platform.runLater(() ->
                                        recentMetricsContainer.getChildren().remove(j));
                                break;
                            }
                        }
                    }
                }

                //genera el gráfico en otro hilo
                Thread t = new Thread(() -> generateMetricGraphics());
                t.start();
            }
        });
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

        showDoctorInfo();
    }

    /********************************** graficos ******************************************/

    private void generateMetricGraphics() {
        ObservableList<PressureMetric> pressureMetrics = FXCollections.observableArrayList();
        ObservableList<GlucoseMetric> glucoseMetrics = FXCollections.observableArrayList();
        ObservableList<HeartRateMetric> heartRateMetrics = FXCollections.observableArrayList();
        ObservableList<WeightMetric> bmiMetrics = FXCollections.observableArrayList();

        for (Metric i: recent) {
            if (i instanceof PressureMetric) pressureMetrics.add((PressureMetric) i);
            else if (i instanceof GlucoseMetric) glucoseMetrics.add((GlucoseMetric) i);
            else if (i instanceof HeartRateMetric) heartRateMetrics.add((HeartRateMetric) i);
            else if (i instanceof WeightMetric) bmiMetrics.add((WeightMetric) i);
        }

        LineChart<String, Number> pressure = getPressureChart(pressureMetrics);
        LineChart<String, Number> glucose = getGlucoseChart(glucoseMetrics);
        LineChart<String, Number> heartRate = getHeartRateChart(heartRateMetrics);
        LineChart<String, Number> bmi = getBmiChart(bmiMetrics);

        Platform.runLater(() -> {
            patientPressureChartContainer.getChildren().clear();
            patientPressureChartContainer.getChildren().add(pressure);

            patientGlucoseChartContainer.getChildren().clear();
            patientGlucoseChartContainer.getChildren().add(glucose);

            patientHeartRateChartContainer.getChildren().clear();
            patientHeartRateChartContainer.getChildren().add(heartRate);

            patientWeightChartContainer.getChildren().clear();
            patientWeightChartContainer.getChildren().add(bmi);

        });
    }

    private String getDayName(LocalDate date) {
        switch (date.getDayOfWeek()) {
            case MONDAY -> {
                return "LUN";
            }
            case TUESDAY -> {
                return "MAR";
            }
            case WEDNESDAY -> {
                return "MIE";
            }
            case THURSDAY -> {
                return "JUE";
            }
            case FRIDAY -> {
                return "VIE";
            }
            case SATURDAY -> {
                return "SAB";
            }
            case SUNDAY -> {
                return "DOM";
            }

            default -> {
                return "";
            }
        }
    }

    private LineChart<String, Number> getPressureChart(ObservableList<PressureMetric> pressureMetrics) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        LineChart<String,Number> chart = new LineChart<String,Number>(xAxis,yAxis);

        XYChart.Series sys = new XYChart.Series();
        sys.setName("Presión sistólica");
        XYChart.Series dia = new XYChart.Series();
        dia.setName("Presión diastólica");

        for (PressureMetric i: pressureMetrics) {
            sys.getData().add(new XYChart.Data(getDayName(i.getDateObj()), i.getSystolic()));
            dia.getData().add(new XYChart.Data(getDayName(i.getDateObj()), i.getDiastolic()));
        }

        chart.getData().addAll(sys, dia);

        return chart;
    }

    private LineChart<String, Number> getGlucoseChart(ObservableList<GlucoseMetric> glucoseMetrics) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        LineChart<String,Number> chart = new LineChart<String,Number>(xAxis,yAxis);

        XYChart.Series series = new XYChart.Series();
        series.setName("Glucosa");

        for (GlucoseMetric i: glucoseMetrics) {
            series.getData().add(new XYChart.Data(getDayName(i.getDateObj()), i.getGlucose()));
        }

        chart.getData().add(series);

        return chart;
    }

    private LineChart<String, Number> getHeartRateChart(ObservableList<HeartRateMetric> heartRateMetrics) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        LineChart<String,Number> chart = new LineChart<String,Number>(xAxis,yAxis);

        XYChart.Series series = new XYChart.Series();
        series.setName("Frecuencia cardíaca");

        for (HeartRateMetric i: heartRateMetrics) {
            series.getData().add(new XYChart.Data(getDayName(i.getDateObj()), i.getHeartRate()));
        }

        chart.getData().add(series);

        return chart;
    }

    private LineChart<String, Number> getBmiChart(ObservableList<WeightMetric> bmiMetrics) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        LineChart<String,Number> chart = new LineChart<String,Number>(xAxis,yAxis);

        XYChart.Series series = new XYChart.Series();
        series.setName("Indice de masa corporal");

        for (WeightMetric i: bmiMetrics) {
            series.getData().add(new XYChart.Data(getDayName(i.getDateObj()), i.getBmi()));
        }

        chart.getData().add(series);

        return chart;
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
