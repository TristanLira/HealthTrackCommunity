package com.example.healthtrackcommunity;

import com.example.healthtrackcommunity.controls.MonitoringRequestDisplay;
import com.example.healthtrackcommunity.controls.PatientDisplay;
import com.example.healthtrackcommunity.models.*;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;
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
    public Button filterPatientsList;
    public VBox patientsListContainer;

    //SECCIÓN DE SOLICITUDES
    public VBox pendingRequestsSection;
    public VBox requestsContainer;

    //SECCIÓN DE MÉTRICAS
    public VBox patientMetricsSection;
    public ComboBox<Patient> patientSelector;
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
    private PatientDAO allPatientsDAO;
    private ObservableList<Patient> unmonitoredPatients;


    //solicitudes
    private MonitoringRequestDAO requestDAO;
    private ObservableList<MonitoringRequest> requests;

    public void initialize() {
    }

    public void setLoggedUser(DoctorDAO dao, Doctor logged, PatientDAO allPatientsDAO) {
        this.logged = logged;
        this.doctorDAO = dao;

        this.allPatientsDAO = allPatientsDAO;
        unmonitoredPatients = allPatientsDAO.getAll();

        patientDAO = new PatientDAO(logged);
        patients = patientDAO.getAll();

        requestDAO = new MonitoringRequestDAO(logged);
        requests = requestDAO.getAll();

        doctorNameLabel.setText("Dr. " + logged.getName());
        doctorSpecializationLabel.setText(logged.getSpecialization());

        //getUnmonitoredPatients();
        showPatients();
        showPendingRequests();
    }

    public void getUnmonitoredPatients() {
        unmonitoredPatients = FXCollections.observableArrayList();

        //use una query sencilla en vez de un dao para evitar complejidad
        FirebaseConnection.getDB()
                .getReference("patients")
                .orderByChild("doctorId")
                .equalTo("")
                .addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot snapshot, String s) {
                Patient p = snapshot.getValue(Patient.class);
                unmonitoredPatients.add(p);
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String s) {
                Patient p = snapshot.getValue(Patient.class);
                unmonitoredPatients.remove(p);
                unmonitoredPatients.add(p);
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                Patient p = snapshot.getValue(Patient.class);
                unmonitoredPatients.remove(p);
            }

            @Override public void onChildMoved(DataSnapshot snapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        });
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

    /******************************** SECCIÓN DE PACIENTES *****************************************/

    private void showPatients() {

        //agrega las que ya estaban en la gui
        for (Patient i: patients) {
            PatientDisplay p = new PatientDisplay(i);
            addRemoveEvent(p);
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
