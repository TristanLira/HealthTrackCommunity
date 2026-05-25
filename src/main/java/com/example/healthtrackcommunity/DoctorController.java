package com.example.healthtrackcommunity;

import com.example.healthtrackcommunity.controls.*;
import com.example.healthtrackcommunity.models.*;
import config.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.checkerframework.checker.units.qual.C;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class DoctorController {

    public ScrollPane mainScrollPane;
    public StackPane mainContent;
    public Label doctorNameLabel;
    public Label doctorSpecializationLabel;

    //SECCIÓN DE DASHBOARD
    public VBox dashboardSection;
    public Label totalPatientsLabel;
    public Label activeAlertsLabel;
    public VBox alertsContainer;

    //SECCION DE PACIENTES
    public VBox patientsListSection;
    public TextField searchPatientField;
    public Button filterPatientsListBtn;
    public VBox patientsListContainer;

    //SECCIÓN DE SOLICITUDES
    public VBox pendingRequestsSection;
    public VBox requestsContainer;

    //SECCIÓN DE MÉTRICAS
    public VBox patientMetricsSection;
    public TabPane metricsTab;
    public Label currentPatientMetricLabel;
    public VBox bloodPressureMetricContainer;
    public VBox glucoseMetricContainer;
    public VBox heartRateMetricContainer;
    public VBox weightMetricContainer;

    //SECCIÓN DE GRAFICOS
    public VBox patientChartsSection;
    public TabPane chartsTab;
    public Label currentPatientChartLabel;
    public VBox bloodPressureChartContainer;
    public VBox glucoseChartContainer;
    public VBox heartRateChartContainer;
    public VBox weightChartContainer;

    //SECCIÓN DE COMENTARIOS
    public VBox commentsSection;
    public Label currentPatientCommentLabel;
    public VBox doctorCommentsContainer;
    public Button filterCommentsBtn;
    public Button removeCommentsFilterBtn;


    //DAOs y doctor loggeado
    private DoctorDAO doctorDAO;
    private Doctor logged;

    //pacientes monitoreados por el doctor
    private PatientDAO patientDAO;
    private ObservableList<Patient> patients;

    //lista de todos los pacientes
    private ObservableList<Patient> unmonitoredPatients;

    //solicitudes
    private MonitoringRequestDAO requestDAO;
    private ObservableList<MonitoringRequest> requests;

    //paciente visualizado
    private Patient current;
    private ObservableList<Metric> recent;
    private MetricDAO currentPressureDAO;
    private MetricDAO currentGlucoseDAO;
    private MetricDAO currentHeartRateDAO;
    private MetricDAO currentWeightDAO;

    //alertas para el doctor
    private MetricAlertDAO alertDAO;
    private ObservableList<MetricAlert> alerts;

    //comentarios
    private CommentDAO commentDAO;
    private ObservableList<Comment> comments;

    public void initialize() {
    }

    public void setLoggedUser(DoctorDAO dao, Doctor logged, PatientDAO allPatientsDAO) {
        this.logged = logged;
        this.doctorDAO = dao;

        unmonitoredPatients = allPatientsDAO.getAll();

        patientDAO = new PatientDAO(logged);
        patients = patientDAO.getAll();

        requestDAO = new MonitoringRequestDAO(logged);
        requests = requestDAO.getAll();

        doctorNameLabel.setText("Dr. " + logged.getName());
        doctorSpecializationLabel.setText(logged.getSpecialization());

        currentPatientMetricLabel.setText("Sin paciente seleccionado.");
        currentPatientChartLabel.setText("Sin paciente seleccionado.");
        currentPatientCommentLabel.setText("Sin paciente seleccionado.");

        alertDAO = new MetricAlertDAO(logged);
        alerts = alertDAO.getAll();

        commentDAO = new CommentDAO(logged);
        comments = commentDAO.getAll();

        //getUnmonitoredPatients();
        showPatients();
        showPendingRequests();
        showAlerts();
        showComments();
    }

    /******************************** MOSTRAR SECCIONES *****************************************/

    public void showDashboard(ActionEvent event) {
        hideAllSections();
        dashboardSection.setVisible(true);
        dashboardSection.setManaged(true);
        totalPatientsLabel.setText(patients.size() + "");
    }

    public void showPatientsList(ActionEvent event) {
        hideAllSections();
        patientsListSection.setVisible(true);
        patientsListSection.setManaged(true);
    }

    public void showCommentsSection(ActionEvent actionEvent) {
        hideAllSections();
        commentsSection.setVisible(true);
        commentsSection.setManaged(true);
    }

    public void showPendingRequests(ActionEvent event) {
        hideAllSections();
        pendingRequestsSection.setVisible(true);
        pendingRequestsSection.setManaged(true);
    }

    public void showPatientMetrics(ActionEvent event) {
        hideAllSections();
        patientMetricsSection.setVisible(true);
        patientMetricsSection.setManaged(true);
    }

    public void showPatientCharts(ActionEvent event) {
        hideAllSections();
        patientChartsSection.setVisible(true);
        patientChartsSection.setManaged(true);

        Thread t = new Thread(() -> {
            generateMetricGraphics();
            reloadTab(chartsTab);
        });
        t.start();
    }

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
            i.setManaged(false);
            i.setVisible(false);
        }
    }

    private void reloadTab(TabPane pane) {
        Tab selected = pane.getSelectionModel().getSelectedItem();
        pane.getSelectionModel().clearSelection();
        pane.getSelectionModel().select(selected);
    }

    /******************************** DASHBOARD *****************************************/

    private void showAlerts() {
        activeAlertsLabel.setText("" + alerts.size());

        //alertas cargadas
        for (MetricAlert i: alerts) {
            MetricAlertDisplay d = new MetricAlertDisplay(i, patientDAO.get(i.getPatientId()));
            addAlertEvents(d);
            Platform.runLater(() ->
                    alertsContainer.getChildren().addFirst(d));
        }

        //futuras alertas
        alerts.addListener((ListChangeListener<MetricAlert>) change -> {
            while (change.next()) {
                activeAlertsLabel.setText("" + alerts.size());

                if (change.wasAdded()) {
                    for (MetricAlert i: change.getAddedSubList()) {
                        MetricAlertDisplay d = new MetricAlertDisplay(i, patientDAO.get(i.getPatientId()));
                        addAlertEvents(d);
                        Platform.runLater(() ->
                                alertsContainer.getChildren().addFirst(d));
                        //igual que con las metricas, lo añade al inicio para que se muestren en orden de carga a la bd
                    }
                } else if (change.wasRemoved()) {
                    for (MetricAlert i: change.getRemoved()) {
                        removeAlertDisplay(i);
                    }
                }

            }
        });
    }

    private void removeAlertDisplay(MetricAlert m) {
        for (Node i: alertsContainer.getChildren()) {
            if (!(i instanceof MetricAlertDisplay)) continue;
            if ( ((MetricAlertDisplay) i).displaysAlert(m) ) {
                Platform.runLater(() -> alertsContainer.getChildren().remove(i));
                return;
            }
        }
    }

    private void addAlertEvents(MetricAlertDisplay d) {
        d.getDismissBtn().setOnAction(event -> {
            AlertUtil.showConfirmationAlert(
                    "Eliminar alerta",
                    "¿Realmente quiere eliminar esta alerta?",
                    () -> alertDAO.delete(d.getAlert()));
        });

        d.getCommentBtn().setOnAction(event -> {
            createCommentAlert(patientDAO.get(d.getPatientId()),
                     "Alerta del " + d.getAlert().getDate(),
                    "");
        });
    }

    /******************************** SECCIÓN DE PACIENTES *****************************************/

    private void showPatients() {
        //agrega las que ya estaban en la lista
        /*patientsListContainer.getChildren().clear();

        for (Patient i: patients) {
            PatientDisplay p = new PatientDisplay(i);
            addPatientEvents(p);
            Platform.runLater(() -> {
                totalPatientsLabel.setText(patients.size() + "");
                patientsListContainer.getChildren().add(p);
            });
        }*/

        //por alguna razón no carga la lista de pacientes a la primera si no se hace con el runnable

        patientsListContainer.getChildren().clear();

        Runnable refresh = () -> {
            Platform.runLater(() -> patientsListContainer.getChildren().clear());

            for (Patient i : patients) {
                PatientDisplay p = new PatientDisplay(i);
                addPatientEvents(p);
                Platform.runLater(() ->
                        patientsListContainer.getChildren().add(p));
            }

            Platform.runLater(() ->
                    totalPatientsLabel.setText(patients.size() + ""));
        };

        refresh.run();

        //evento para los futuros pacientes agregados
        patients.addListener((ListChangeListener<Patient>) change -> {
            while(change.next()) {
                Platform.runLater(() ->
                        totalPatientsLabel.setText(patients.size() + ""));

                if (change.wasAdded()) {

                    for (Patient i: change.getAddedSubList()) {
                        if (i == null) {
                            System.out.println("Paciente recibido (null)");
                            continue;
                        }
                        System.out.println("Paciente recibido: " + i.getName() + "(" + i.getDoctorId() + ")");
                        PatientDisplay p = new PatientDisplay(i);
                        addPatientEvents(p);
                        Platform.runLater(() ->
                                patientsListContainer.getChildren().add(p));
                    }

                } else if (change.wasRemoved()) {

                    for (Patient i: change.getRemoved()) {
                        for (Node j: patientsListContainer.getChildren()) {
                            if (!(j instanceof PatientDisplay)) continue;
                            if ( ((PatientDisplay) j).displaysPatient(i) ) {
                                Platform.runLater(() ->
                                        patientsListContainer.getChildren().remove(j));
                                break;
                            }
                        }
                    }

                }
            }
        });
    }

    private void addPatientEvents(PatientDisplay p) {
        addRemoveEvent(p);
        addVisualizeEvent(p);
        addCommentEvent(p);
    }

    private void removePatient(String id) {
        Patient p = patientDAO.get(id);
        if (p == null) return;
        p.setDoctorId("");
        patientDAO.update(p);
    }


    //FILTROS

    public void filterPatientsList(ActionEvent event) {
        String filter = searchPatientField.getText();
        searchPatientField.clear();

        removeFilters();

        //inicia el filtro en otro hilo para no congelar el de javafx
        Thread t = new Thread(() -> startFilter(filter));
        t.start();
    }

    public void startFilter(String filter) {
        for (Node i: patientsListContainer.getChildren()) {
            if (!(i instanceof PatientDisplay)) continue;

            if ( ((PatientDisplay)i).getPatientName().contains(filter) ||
                    ((PatientDisplay)i).getPatientEmail().contains(filter))
                continue;

            Platform.runLater(() -> {
                i.setVisible(false);
                i.setManaged(false);
            });
        }
    }

    public void removePatientsFilters(ActionEvent event) {
        Thread t = new Thread(() -> removeFilters());
        t.start();
    }

    public void removeFilters() {
        for (Node i: patientsListContainer.getChildren()) {
            Platform.runLater(() -> {
                i.setVisible(true);
                i.setManaged(true);
            });
        }
    }

    //EVENTOS

    private void addCommentEvent(PatientDisplay p) {
        p.getCommentBtn().setOnAction(event -> {
            Patient patient = patientDAO.get(p.getPatientId());
            if (patient != null) createCommentAlert(patient);
        });
    }

    private void addRemoveEvent(PatientDisplay p) {
        p.getRemoveBtn().setOnAction(event -> {
            AlertUtil.showConfirmationAlert(
                    "Eliminar paciente",
                    "¿Está seguro que quiere eliminar este paciente?",
                    () -> removePatient(p.getPatientId()));
        });
    }

    //VISUALIZAR

    //este evento cargará todas las métricas de los pacientes
    private void addVisualizeEvent(PatientDisplay p) {
        p.getVisualizeBtn().setOnAction(event -> {
            current = patientDAO.get(p.getPatientId());
            String currentPatientStr = current.getName() + " (" + current.getEmail() + ")";
            currentPatientMetricLabel.setText(currentPatientStr);
            currentPatientChartLabel.setText(currentPatientStr);
            currentPatientCommentLabel.setText(currentPatientStr);

            //iniciar los daos
            currentPressureDAO = new MetricDAO(current, MetricDAO.PRESSURE);
            currentGlucoseDAO = new MetricDAO(current, MetricDAO.GLUCOSE);
            currentHeartRateDAO = new MetricDAO(current, MetricDAO.HEART_RATE);
            currentWeightDAO = new MetricDAO(current, MetricDAO.WEIGHT);

            //inicializar la lista de mediciones recientes y los gráficos
            recent = (new RecentMetrics(current)).getRecent();
            generateChartsOnRecentChanged();

            //limpiar los containers
            bloodPressureMetricContainer.getChildren().clear();
            glucoseMetricContainer.getChildren().clear();
            heartRateMetricContainer.getChildren().clear();
            weightMetricContainer.getChildren().clear();

            //inicializar los eventos para agregar los displays
            loadMetricDisplay(currentPressureDAO.getAll(), bloodPressureMetricContainer, PressureMetric.class);
            loadMetricDisplay(currentGlucoseDAO.getAll(), glucoseMetricContainer, GlucoseMetric.class);
            loadMetricDisplay(currentHeartRateDAO.getAll(), heartRateMetricContainer, HeartRateMetric.class);
            loadMetricDisplay(currentWeightDAO.getAll(), weightMetricContainer, WeightMetric.class);
        });
    }

    private void generateChartsOnRecentChanged() {
        recent.addListener((ListChangeListener<? super Metric>) change -> {
            while (change.next()) {
                Thread t = new Thread(() -> generateMetricGraphics());
                t.start();
            }
        });
    }

    private void loadMetricDisplay(ObservableList<Metric> list, VBox container, Class<? extends Metric> metricClass) {
        //cargar métricas ya existentes
        for (Metric i: list) {
            if (i.getClass() != metricClass) continue;

            MetricDisplay display = getMetricDisplay(i);
            display.hideTitle();
            container.getChildren().addFirst(display);
        }


        //cargar futuras métricas
        list.addListener((ListChangeListener<? super Metric>) change -> {

            while (change.next()) {
                if (change.wasAdded()) {

                    for (Metric i: change.getAddedSubList()) {
                        if (i.getClass() != metricClass) continue; //no debería haber otro tipo de métricas en esta lista, pero por si acaso
                        MetricDisplay display = getMetricDisplay(i);
                        display.hideTitle();
                        Platform.runLater(() -> {
                            container.getChildren().addFirst(display);
                            reloadTab(metricsTab);
                        });
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

    private MetricDisplay getMetricDisplay(Metric m) {
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

    /******************************** SECCIÓN DE SOLICITUDES *****************************************/

    private void showPendingRequests() {
        //agregar solicitudes
        for (MonitoringRequest i: requests) {
            Patient patient = getUnmonitoredPatient(i.getPatientId());
            if (patient == null) continue;

            MonitoringRequestDisplay r = new MonitoringRequestDisplay(i, patient);
            addRequestDisplayEvents(r, patient);
            requestsContainer.getChildren().add(r);
        }

        //futuras solicitudes
        requests.addListener((ListChangeListener<MonitoringRequest>) change -> {
            while (change.next()) {

                if (change.wasAdded()) {
                    System.out.print("NUEVA SOLICITUD RECIBIDA: ");

                    for (MonitoringRequest i: change.getAddedSubList()) {
                        Patient patient = getUnmonitoredPatient(i.getPatientId());
                        if (patient == null) {
                            System.out.println("SOLICITUD ES NULL");
                            continue;
                        }

                        System.out.println(patient.getName() + "(" + patient.getEmail() + ")");

                        MonitoringRequestDisplay r = new MonitoringRequestDisplay(i, patient);
                        addRequestDisplayEvents(r, patient);

                        Platform.runLater(() ->
                                requestsContainer.getChildren().add(r));
                    }

                } else if (change.wasRemoved()) {

                    for (MonitoringRequest i: change.getRemoved()) {

                        for (Node j: requestsContainer.getChildren()) {
                            if (!(j instanceof MonitoringRequestDisplay)) continue;

                            if ( ((MonitoringRequestDisplay) j).displaysRequest(i) ) {
                                Platform.runLater(() ->
                                        requestsContainer.getChildren().remove(j));
                                break;
                            }
                        }

                    }

                }

            }
        });
    }

    private Patient getUnmonitoredPatient(String id) {
        for (Patient i: unmonitoredPatients) {
            if (i.getId().equals(id) && i.getDoctorId().isEmpty()) return i;
        }
        return null;
    }

    private void addRequestDisplayEvents(MonitoringRequestDisplay r, Patient p) {
        r.getAcceptBtn().setOnAction(event -> {
            p.setDoctorId(logged.getId());
            patientDAO.update(p);
            requestDAO.delete(r.getRequest());
        });

        r.getDeclineBtn().setOnAction(event -> {
            AlertUtil.showConfirmationAlert(
                    "Rechazar solicitud de seguimiento médico",
                    "¿Está seguro que quiere rechazar la solicitud?",
                    () -> requestDAO.delete(r.getRequest()));
        });
    }


    /******************************** SECCIÓN DE GRAFICOS *****************************************/

    private void generateMetricGraphics() {
        if (recent == null) return;

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

        pressure.getStyleClass().add("pressure-chart");
        glucose.getStyleClass().add("glucose-chart");
        heartRate.getStyleClass().add("heart-rate-chart");
        bmi.getStyleClass().add("weight-chart");

        Platform.runLater(() -> {
            bloodPressureChartContainer.getChildren().clear();
            bloodPressureChartContainer.getChildren().add(pressure);

            glucoseChartContainer.getChildren().clear();
            glucoseChartContainer.getChildren().add(glucose);

            heartRateChartContainer.getChildren().clear();
            heartRateChartContainer.getChildren().add(heartRate);

            weightChartContainer.getChildren().clear();
            weightChartContainer.getChildren().add(bmi);

            reloadTab(chartsTab);
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


    /******************************** CREAR Y MOSTRAR COMENTARIOS *****************************************/

    //comentario respondiendo a una alerta
    private void createCommentAlert(Patient p, String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Comentario");
        alert.setHeaderText("Crear un nuevo comentario para " + p.getName());
        AlertUtil.addAlertCss(alert);

        TextField commentTitle = new TextField();
        TextArea commentContent = new TextArea();
        commentTitle.setText(title);
        commentContent.setText(content);

        GridPane grid = new GridPane();

        grid.add(new Label("Título"), 0,0);
        grid.add(commentTitle, 1,0);

        grid.add(new Label("Contenido"), 0,1);
        grid.add(commentContent, 1,1);

        grid.setVgap(10);
        grid.setHgap(10);

        alert.getDialogPane().setContent(grid);

        //activar botón
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK){
            Comment c = new Comment(p.getId(), logged.getId(), commentTitle.getText(), commentContent.getText(), true);
            commentDAO.create(c,
                    () -> Platform.runLater(() ->
                            AlertUtil.showInfoAlert("Comentario creado", "El comentario fue creado correctamente.")),
                    () -> Platform.runLater(() ->
                            AlertUtil.showErrorAlert("Comentario no creado", "No se pudo enviar el comentario. Inténtelo de nuevo más tarde."))
            );
        } else {
            alert.close();
        }
    }

    //comentario normal
    private void createCommentAlert(Patient p) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Comentario");
        alert.setHeaderText("Crear un nuevo comentario para " + p.getName());
        AlertUtil.addAlertCss(alert);

        TextField commentTitle = new TextField();
        TextArea commentContent = new TextArea();

        GridPane grid = new GridPane();

        grid.add(new Label("Título"), 0,0);
        grid.add(commentTitle, 1,0);

        grid.add(new Label("Contenido"), 0,1);
        grid.add(commentContent, 1,1);

        grid.setVgap(10);
        grid.setHgap(10);

        alert.getDialogPane().setContent(grid);

        //activar botón
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK){
            Comment c = new Comment(p.getId(), logged.getId(), commentTitle.getText(), commentContent.getText(), false);
            commentDAO.create(c,
                    () -> Platform.runLater(() ->
                            AlertUtil.showInfoAlert("Comentario creado", "El comentario fue creado correctamente.")),
                    () -> Platform.runLater(() ->
                            AlertUtil.showErrorAlert("Comentario no creado", "No se pudo enviar el comentario. Inténtelo de nuevo más tarde."))
            );
        } else {
            alert.close();
        }
    }

    public void showComments() {
        for (Comment i: comments) {
            Patient p = patientDAO.get(i.getPatientId());
            if (p == null) continue;
            CommentDisplay display = new CommentDisplay(i, p);
            doctorCommentsContainer.getChildren().add(display);
        }

        comments.addListener((ListChangeListener<Comment>) change -> {
            while (change.next()) {

                if (change.wasAdded()) {
                    for (Comment i: change.getAddedSubList()) {
                        Patient p = patientDAO.get(i.getPatientId());
                        if (p == null) continue;
                        CommentDisplay display = new CommentDisplay(i, p);
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

    public void filterComments(ActionEvent actionEvent) {
        Thread t = new Thread(() -> {
            for (Node i: doctorCommentsContainer.getChildren()) {
                if (!(i instanceof CommentDisplay)) continue;

                String id = ((CommentDisplay) i).getComment().getPatientId();
                Patient p = patientDAO.get(id);

                boolean isForPatient = p != null && p.equals(current);
                i.setVisible(isForPatient);
                i.setManaged(isForPatient);
            }
        });

        t.start();
    }

    public void removeCommentsFilter(ActionEvent actionEvent) {
        Thread t = new Thread(() -> {
            for (Node i: doctorCommentsContainer.getChildren()) {
                i.setVisible(true);
                i.setManaged(true);
            }
        });

        t.start();
    }
}
