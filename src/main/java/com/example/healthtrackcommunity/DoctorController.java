package com.example.healthtrackcommunity;

import com.example.healthtrackcommunity.controls.*;
import com.example.healthtrackcommunity.models.*;
import config.*;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
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
    public VBox alertPatientsContainer;

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
    private MetricDAO currentPressureDAO;
    private MetricDAO currentGlucoseDAO;
    private MetricDAO currentHeartRateDAO;
    private MetricDAO currentWeightDAO;

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

        //getUnmonitoredPatients();
        showPatients();
        showPendingRequests();
    }

    /******************************** MOSTRAR SECCIONES *****************************************/

    public void showDashboard(ActionEvent event) {
        hideAllSections();
        dashboardSection.setVisible(true);
        dashboardSection.setManaged(true);
    }

    public void showPatientsList(ActionEvent event) {
        hideAllSections();
        patientsListSection.setVisible(true);
        patientsListSection.setManaged(true);
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
    }

    public void showReports(ActionEvent event) {
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

    /******************************** SECCIÓN DE PACIENTES *****************************************/

    private void showPatients() {

        //agrega las que ya estaban en la gui
        for (Patient i: patients) {
            PatientDisplay p = new PatientDisplay(i);
            addRemoveEvent(p);
            addVisualizeEvent(p);
            patientsListContainer.getChildren().add(p);
        }

        //evento para los futuros pacientes agregados
        patients.addListener((ListChangeListener<Patient>) change -> {
            while(change.next()) {

                if (change.wasAdded()) {

                    for (Patient i: change.getAddedSubList()) {
                        if (i == null) {
                            System.out.println("Paciente recibido (null)");
                            continue;
                        }
                        System.out.println("Paciente recibido: " + i.getName() + "(" + i.getDoctorId() + ")");
                        PatientDisplay p = new PatientDisplay(i);
                        addRemoveEvent(p);
                        addVisualizeEvent(p);
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

    private void addRemoveEvent(PatientDisplay p) {
        p.getRemoveBtn().setOnAction(event -> {
            showConfirmationAlert(
                    "Eliminar paciente",
                    "¿Está seguro que quiere eliminar este paciente?",
                    () -> removePatient(p.getPatientId()));
        });
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

    //VISUALIZAR

    //este evento cargará todas las métricas de los pacientes
    private void addVisualizeEvent(PatientDisplay p) {
        p.getVisualizeBtn().setOnAction(event -> {
            current = patientDAO.get(p.getPatientId());
            currentPatientMetricLabel.setText(current.getName() + " (" + current.getEmail() + ")");

            //iniciar los daos
            currentPressureDAO = new MetricDAO(current, MetricDAO.PRESSURE);
            currentGlucoseDAO = new MetricDAO(current, MetricDAO.GLUCOSE);
            currentHeartRateDAO = new MetricDAO(current, MetricDAO.HEART_RATE);
            currentWeightDAO = new MetricDAO(current, MetricDAO.WEIGHT);

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

    private void loadMetricDisplay(ObservableList<Metric> list, VBox container, Class<? extends Metric> metricClass) {
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
            showConfirmationAlert(
                    "Rechazar solicitud de seguimiento médico",
                    "¿Está seguro que quiere rechazar la solicitud?",
                    () -> requestDAO.delete(r.getRequest()));
        });
    }


    /********************* ALERTAS *******************************/

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

    private void showConfirmationAlert(String title, String message, Runnable onConfirm) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(message);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            onConfirm.run();
        } else {
            alert.close();
        }
    }
}
