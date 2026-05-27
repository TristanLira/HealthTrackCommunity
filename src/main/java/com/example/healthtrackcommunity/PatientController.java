package com.example.healthtrackcommunity;

import com.example.healthtrackcommunity.api.WeatherDAO;
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
import java.time.LocalTime;
import java.util.*;

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
    public Button analyzeMetricsBtn;

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
    public VBox weatherChartContainer;

    //seguimiento médico
    public VBox doctorMonitoringSection;
    public VBox doctorCommentsContainer;

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
    
    //tarjeta de clima
    public VBox weatherCard;
    public Label weatherIconLabel;
    public Label weatherDescriptionLabel;
    public Label temperatureLabel;
    public Label maxTempLabel;
    public Label minTempLabel;

    //labels de recomendaciones
    public Label pressureRecommendationLabel;
    public Label heartRateRecommendationLabel;
    public Label weightRecommendationLabel;
    public Label glucoseRecommendationLabel;
    public Label weatherRecommendationLabel;

    //información de pacientes y doctores
    private PatientDAO patientDAO;
    private DoctorDAO doctorDAO;
    private Patient logged;
    private Doctor monitoring;

    private ChartGenerator generator;

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

    //alertas
    private MetricAlertDAO alertDAO;
    private ObservableList<MetricAlert> alerts;

    //comentarios
    private CommentDAO commentDAO;
    private ObservableList<Comment> comments;

    public void initialize() {
        hideAllSections();
        dashboardSection.setManaged(true);
        dashboardSection.setVisible(true);

        generateWeatherChart();
    }

    private void initMetricDAOs() {
        heartRateDAO = new MetricDAO(logged, MetricDAO.HEART_RATE);
        pressureDAO = new MetricDAO(logged, MetricDAO.PRESSURE);
        glucoseDAO = new MetricDAO(logged, MetricDAO.GLUCOSE);
        weightDAO = new MetricDAO(logged, MetricDAO.WEIGHT);
        requestDAO = new MonitoringRequestDAO(logged);
        alertDAO = new MetricAlertDAO(logged);
        commentDAO = new CommentDAO(logged);

        heartRate = heartRateDAO.getAll();
        pressure = pressureDAO.getAll();
        glucose = glucoseDAO.getAll();
        weight = weightDAO.getAll();
        requests = requestDAO.getAll();
        alerts = alertDAO.getAll();
        comments = commentDAO.getAll();
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

        updateAlertLabel();

        showComments();

        createChartGenerator();

        //addMetricsDebug(6);

        /*Thread t = new Thread(() -> createPatientsDebug(100, 100));
        t.start();*/

        //addDoctorsDebug();

        //addCommentsToPatientsDebug();
    }

    private void addMetricsDebug(Patient p, int days) {
        List<PressureMetric> pressureMetrics = new ArrayList<>();
        List<GlucoseMetric> glucoseMetrics = new ArrayList<>();
        List<HeartRateMetric> heartRateMetrics = new ArrayList<>();
        List<WeightMetric> weightMetrics = new ArrayList<>();

        LocalDate today = LocalDate.now();

        for (int i = days; i >= 0; i--) {
            LocalDate day = today.minusDays(i);

            PressureMetric pressureMetric = new PressureMetric(
                    p.getId(),
                    110 + (int)(Math.random() * 20), // sistólica random
                    70 + (int)(Math.random() * 15)   // diastólica random
            );

            GlucoseMetric glucoseMetric = new GlucoseMetric(
                    p.getId(),
                    80 + (int)(Math.random() * 60)
            );

            HeartRateMetric heartRateMetric = new HeartRateMetric(
                    p.getId(),
                    60 + (int)(Math.random() * 41)
            );

            WeightMetric weightMetric = new WeightMetric(
                    p.getId(),
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

        MetricDAO pDAO = new MetricDAO(p, MetricDAO.PRESSURE);
        MetricDAO gDAO = new MetricDAO(p, MetricDAO.GLUCOSE);
        MetricDAO hDAO = new MetricDAO(p, MetricDAO.HEART_RATE);
        MetricDAO wDAO = new MetricDAO(p, MetricDAO.WEIGHT);

        for (Metric j: pressureMetrics) pDAO.create(j);
        for (Metric j: glucoseMetrics) gDAO.create(j);
        for (Metric j: heartRateMetrics) hDAO.create(j);
        for (Metric j: weightMetrics) wDAO.create(j);
    }

    private void addMetricsDebug(int days) {
        addMetricsDebug(logged, days);
    }

    private void createPatientsDebug(int patientsNum, int metricsNum) {
        String[] names = {
                "Juan", "María", "José", "Ana", "Luis", "Carmen", "Pedro", "Sofía",
                "Miguel", "Lucía", "Fernando", "Valeria", "Carlos", "Elena", "Jorge",
                "Daniela", "Ricardo", "Paula", "Andrés", "Camila", "Diego", "Fernanda",
                "Manuel", "Gabriela", "Eduardo", "Natalia", "Alejandro", "Patricia",
                "Francisco", "Andrea", "Raúl", "Mónica", "Sebastián", "Victoria"
        };

        String[] lastNames = {
                "Pérez", "García", "Martínez", "López", "Hernández", "González",
                "Rodríguez", "Sánchez", "Ramírez", "Cruz", "Flores", "Torres",
                "Rivera", "Gómez", "Díaz", "Morales", "Vargas", "Castillo",
                "Ortiz", "Reyes", "Jiménez", "Ruiz", "Mendoza", "Aguilar",
                "Ramos", "Silva", "Romero", "Navarro", "Delgado", "Medina"
        };

        Random random = new Random();
        Set<String> usedNames = new HashSet<>();

        for (int i = 1; i <= patientsNum; i++) {

            String fullName;

            //evitar nombres completos repetidos
            do {
                String firstName = names[random.nextInt(names.length)];
                String lastName1 = lastNames[random.nextInt(lastNames.length)];
                String lastName2 = lastNames[random.nextInt(lastNames.length)];

                fullName = firstName + " " + lastName1 + " " + lastName2;

            } while (usedNames.contains(fullName));

            usedNames.add(fullName);

            Patient p = new Patient(
                    "paciente" + i + "@gmail.com",
                    "password1",
                    fullName
            );

            patientDAO.create(p,
                    () -> addMetricsDebug(p, 100),
                    () -> System.out.println("Error al crear: " + p.getEmail()),
                    () -> System.out.println("Email ya registrado: " + p.getEmail())
            );
        }
    }

    private void addDoctorsDebug() {
        ObservableList<Patient> copy = FXCollections.observableArrayList(patients);

        Random random = new Random();

        for (Patient i: copy) {
            if (!i.getDoctorId().isEmpty()) continue;

            Doctor rdoctor = null;

            while (rdoctor == null) rdoctor = doctors.get(random.nextInt(doctors.size()) );

            i.setDoctorId(rdoctor.getId());
            patientDAO.update(i);
        }

        System.out.println("DOCTORES AGREGADOS");
    }

    private void addCommentsToPatientsDebug() {
        ObservableList<Patient> copy = FXCollections.observableArrayList(patients);
        for (Patient i: copy) createComments(i, 10);
    }

    private void createComments(Patient p, int days) {
        if (p.getDoctorId().isEmpty()) return;

        List<String> commentTitles = List.of(
                "Seguimiento General",
                "Estado de Salud",
                "Monitoreo Preventivo",
                "Revisión de Métricas",
                "Control de Signos Vitales",
                "Observación Médica",
                "Recomendación General",
                "Evaluación Periódica",
                "Seguimiento de Rutina",
                "Análisis de Resultados",
                "Control Médico",
                "Evaluación de Progreso",
                "Estado Actual",
                "Revisión Preventiva",
                "Observaciones del Médico",
                "Chequeo General",
                "Revisión de Indicadores",
                "Monitoreo de Salud",
                "Seguimiento Clínico",
                "Control Preventivo"
        );

        List<String> medicalComments = List.of(
                "Tus signos vitales se encuentran dentro de rangos normales. Continúa con tus hábitos actuales.",
                "Se recomienda mantener una alimentación equilibrada y una hidratación adecuada.",
                "Tus métricas muestran estabilidad general. Continúa con el monitoreo regular.",
                "Se observan ligeras variaciones que no parecen preocupantes por el momento.",
                "Es importante mantener actividad física moderada de forma constante.",
                "No se detectan alteraciones importantes en los registros recientes.",
                "Sería recomendable continuar con revisiones periódicas para seguimiento.",
                "Procura mantener horarios de sueño regulares y evitar estrés excesivo.",
                "Tus resultados reflejan una evolución favorable en comparación con registros anteriores.",
                "Hay pequeños cambios en algunos indicadores que conviene seguir observando.",
                "Continúa registrando tus mediciones para llevar un mejor control.",
                "Se recomienda reducir el consumo de alimentos altos en azúcar y sodio.",
                "Tu estado general parece estable con base en las métricas actuales.",
                "Mantén hábitos saludables y evita periodos prolongados de inactividad.",
                "Las mediciones actuales permanecen dentro de valores esperados.",
                "Es recomendable repetir algunas mediciones en próximos días para confirmar estabilidad.",
                "No se identifican señales de alerta inmediatas, pero es importante continuar el seguimiento.",
                "Se observa consistencia en los registros, lo cual es positivo para el monitoreo.",
                "Podría beneficiarte mantener una rutina más constante de actividad física.",
                "Tus métricas reflejan un estado general adecuado hasta el momento."
        );


        Random r = new Random();

        for (int i = days; i >= 0; i--) {
            //fecha y hora
            LocalDate day = LocalDate.now().minusDays(i);
            LocalTime randomTime = LocalTime.of(
                    r.nextInt(24), // hora (0–23)
                    r.nextInt(60), // minuto (0–59)
                    r.nextInt(60)  // segundo (0–59)
            );

            String title = commentTitles.get( r.nextInt(commentTitles.size()) );
            String comment = medicalComments.get( r.nextInt(medicalComments.size()) );

            Comment c = new Comment(p.getId(), p.getDoctorId(), title, comment, false);
            c.setDate(day.toString());
            c.setTime(randomTime.toString());

            commentDAO.create(c);
        }
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

        /*aunque cada que se detecta una nueva medición en la consulta de mediciones recientes se actualizan los gráficos, a veces no se
        * cargan todos los datos. Para arreglar eso se vuelven a generar los gráficos entrando a la sección de gráficos. Al generar los
        * gráficos en ambas situaciones se muestran correctamente siempre, pero se actualizarán en tiempo real si se recibe una nueva métrica*/
        Thread t = new Thread(() -> generator.generateMetricsCharts());
        t.start();
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
            AlertUtil.showErrorAlert("Datos inválidos", "Los datos ingresados no son validos. Por favor ingrese solo números.");
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
            AlertUtil.showErrorAlert("Datos inválidos", "Los datos ingresados no son validos. Por favor ingrese solo números.");
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
            AlertUtil.showErrorAlert("Datos inválidos", "Los datos ingresados no son validos. Por favor ingrese solo números.");
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
            AlertUtil.showErrorAlert("Datos inválidos", "Los datos ingresados no son validos. Por favor ingrese solo números.");
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

    /********************************** generar alertas ******************************************/

    //cada que recibe una alerta en la lista actualiza la label
    private void updateAlertLabel() {
        showTendencies();
        alerts.addListener((ListChangeListener<MetricAlert>) change -> {
            while (change.next()) {
                Platform.runLater(() -> showTendencies());
            }
        });
    }

    public void analyzeMetrics(ActionEvent event) {
        if (monitoring == null) {
            AlertUtil.showErrorAlert("Sin médico asignado", "Necesitas un médico asignado antes de analizar tus métricas.");
            return;
        }

        //hace el análisis en un hilo nuevo para no bloquear el de javafx
        Thread t = new Thread(() -> {
            HealthTrendAnalyzer analyzer = new HealthTrendAnalyzer(recent);

            if (analyzer.hasDangerousTendencies()) {
                MetricAlert a = analyzer.getAlert(logged.getId(), monitoring.getId());
                registerAlert(a);
            } else {
                Platform.runLater(() -> AlertUtil.showInfoAlert("Métricas correctas", "Tus métricas se encuentran en rangos normales. ¡Continúa así!"));
            }

            Platform.runLater(() -> {
                pressureRecommendationLabel.setText(analyzer.getPressureRecommendation());
                glucoseRecommendationLabel.setText(analyzer.getGlucoseRecommendation());
                heartRateRecommendationLabel.setText(analyzer.getHeartRateRecommendation());
                weightRecommendationLabel.setText(analyzer.getWeightRecommendation());
            });
        });

        t.start();
    }

    private void registerAlert(MetricAlert a) {
        alertDAO.create(a,
                () -> {
                    Platform.runLater(() ->
                            AlertUtil.showInfoAlert("Tendencia peligrosa", "Se detectó una tendencia peligrosa. Se ha enviado una alerta a tu médico."));
                },
                () -> {
                    Platform.runLater(() ->
                            AlertUtil.showInfoAlert("No se creó la alerta", "Ya se ha registrado una alerta hoy. Espera a mañana para realizar otra."));
                });
    }

    private void showTendencies() {
        if (monitoring == null) healthAlertLabel.setText("Necesitas un médico asignado para realizar un análisis de tus métricas.");
        else if (alerts.isEmpty()) healthAlertLabel.setText("Tus métricas se encuentran en rangos normales.");
        else healthAlertLabel.setText("Tienes " + alerts.size() + " alerta(s). Espera la respuesta de tu médico en la sección de comentarios.");

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

    private void loadRecentDisplays() {
        recent = new RecentMetrics(logged).getRecent(); //obtiene las mediciones recientes

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
                Thread t = new Thread(() -> generator.generateMetricsCharts());
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
            AlertUtil.showErrorAlert("Seleccione médico", "No fue posible mandar la solicitud, por favor seleccione un médico.");
            return;
        }

        MonitoringRequest m = new MonitoringRequest(logged.getId(), d.getId());

        //corre las alertas con runLater porque los callbacks son llamados por firebase en un hilo diferente
        requestDAO.create(m,
                () -> Platform.runLater(
                        () -> AlertUtil.showInfoAlert("Solicitud enviada", "Un solicitud de seguimiento médico fue enviada a " + d + ".")),
                () -> Platform.runLater(
                        () -> AlertUtil.showErrorAlert("Solicitud no enviada", "La solicitud no fue enviada. Por favor inténtelo de nuevo."))
        );

        showDoctorInfo();
    }

    public void showComments() {
        for (Comment i: comments) {
            CommentDisplay display = new CommentDisplay(i);
            doctorCommentsContainer.getChildren().add(display);
        }

        comments.addListener((ListChangeListener<Comment>) change -> {
            while (change.next()) {

                if (change.wasAdded()) {
                    for (Comment i: change.getAddedSubList()) {
                        CommentDisplay display = new CommentDisplay(i);
                        Platform.runLater(() ->
                                doctorCommentsContainer.getChildren().addFirst(display));
                        //se agregan al inicio para que se muestren las más recientes primero como en los demás listeners
                    }
                } else if (change.wasRemoved()) {
                    for (Comment i: change.getRemoved()) {
                        removeCommment(i);
                    }
                }
            }
        });
    }

    private void removeCommment(Comment c) {
        for (Node i: doctorCommentsContainer.getChildren()) {
            if (!(i instanceof CommentDisplay)) continue;
            if ( ((CommentDisplay) i).displaysComment(c) ) {
                Platform.runLater(() -> doctorCommentsContainer.getChildren().remove(i));
            }
        }
    }

    /********************************** graficos ******************************************/

    private void createChartGenerator() {
        generator = new ChartGenerator(patientPressureChartContainer,
                patientGlucoseChartContainer,
                patientHeartRateChartContainer,
                patientWeightChartContainer,
                recent);

        generator.setTab(chartsTab);
    }

    /*generar gráfico de clima*/

    private void generateWeatherChart() {
        WeatherData weather;

        try {
            WeatherDAO dao = new WeatherDAO();
            weather = dao.getWeather(20.5235, -100.8157, 7);

            System.out.println("Temperatura actual: " + weather.getCurrentTemperature() + "°C");
            System.out.println("Clima actual: " + weather.getCurrentWeather());
            System.out.println("\nHistorial:");

            for (WeatherDay day : weather.getHistory()) {
                System.out.println(day);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        LineChart<String, Number> lineChart = getTemperatureChart(weather.getHistory());

        Platform.runLater(() -> {
            weatherChartContainer.getChildren().clear();
            weatherChartContainer.getChildren().add(lineChart);
            weatherRecommendationLabel.setText(weather.getRecommendation());
        });
    }

    private LineChart<String, Number> getTemperatureChart(List<WeatherDay> weatherDays) {

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Temperatura");

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);

        XYChart.Series<String, Number> maxTemps = new XYChart.Series<>();
        maxTemps.setName("Temperatura máxima");

        XYChart.Series<String, Number> minTemps = new XYChart.Series<>();
        minTemps.setName("Temperatura mínima");

        for (WeatherDay day : weatherDays) {
            String label = ChartGenerator.getDayName( LocalDate.parse(day.getDate()) );
            maxTemps.getData().add(new XYChart.Data<>(label, day.getMaxTemperature()));
            minTemps.getData().add(new XYChart.Data<>(label, day.getMinTemperature())
            );
        }

        chart.getData().addAll(maxTemps, minTemps);

        chart.setCreateSymbols(true); // muestra puntos
        chart.setAnimated(false);

        chart.getStyleClass().add("weather-chart");

        return chart;
    }

    public void setWeather(WeatherData weather) {

        temperatureLabel.setText(
                Math.round(weather.getCurrentTemperature())
                        + "°C"
        );

        weatherDescriptionLabel.setText(
                weather.getCurrentWeather()
        );

        WeatherDay today =
                weather.getHistory().getLast();

        maxTempLabel.setText(
                Math.round(today.getMaxTemperature())
                        + "°C"
        );

        minTempLabel.setText(
                Math.round(today.getMinTemperature())
                        + "°C"
        );
    }
}
