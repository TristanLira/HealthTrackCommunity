package com.example.healthtrackcommunity;

import com.example.healthtrackcommunity.controls.DoctorDisplay;
import com.example.healthtrackcommunity.controls.PatientDisplay;
import com.example.healthtrackcommunity.models.Administrator;
import com.example.healthtrackcommunity.models.Doctor;
import com.example.healthtrackcommunity.models.Patient;
import config.AdministratorDAO;
import config.DoctorDAO;
import config.PatientDAO;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminController {

    public Label adminNameLabel;
    public StackPane mainContent;
    
    //dashboard
    public VBox dashboardSection;
    public Label totalDoctorsLabel;
    public Label totalPatientsLabel;
    public Label pendingDoctorsLabel;
    
    //doctores pendientes
    public VBox pendingDoctorsSection;
    public VBox pendingDoctorsContainer;
    public VBox allDoctorsSection;
    public TextField searchDoctorField;
    public VBox allDoctorsContainer;

    //pacientes
    public VBox allPatientsSection;
    public TextField searchPatientField;
    public VBox patientsContainer;

    //daos
    private AdministratorDAO adminDAO;
    private PatientDAO patientDAO;
    private DoctorDAO doctorDAO;

    //listas
    ObservableList<Administrator> admins;
    ObservableList<Patient> patients;
    ObservableList<Doctor> doctors;

    private Administrator logged;

    public void setLoggedUser(AdministratorDAO adminDAO, Administrator logged, PatientDAO patientDAO, DoctorDAO doctorDAO) {
        this.logged = logged;
        this.adminDAO = adminDAO;
        this.doctorDAO = doctorDAO;
        this.patientDAO = patientDAO;

        patients = patientDAO.getAll();
        doctors = doctorDAO.getAll();
        admins = adminDAO.getAll();

        adminNameLabel.setText(logged.getName());

        loadPatients();
        loadDoctors();

        /*Doctor d = new Doctor("doctorprueba@gmail.com", "password1", "Prueba", "Prueba");
        doctorDAO.create(d);*/
    }

    /******************************* MOSTRAR SECCIONES *****************************************/

    public void showDashboard(ActionEvent actionEvent) {
        hideAllSections();
        dashboardSection.setVisible(true);
        dashboardSection.setManaged(true);
    }

    public void showPendingDoctors(ActionEvent actionEvent) {
        hideAllSections();
        pendingDoctorsSection.setVisible(true);
        pendingDoctorsSection.setManaged(true);
    }

    public void showAllDoctors(ActionEvent actionEvent) {
        hideAllSections();
        allDoctorsSection.setVisible(true);
        allDoctorsSection.setManaged(true);
    }

    public void showAllPatients(ActionEvent actionEvent) {
        hideAllSections();
        allPatientsSection.setVisible(true);
        allPatientsSection.setManaged(true);
    }

    private void hideAllSections() {
        for (Node i: mainContent.getChildren()) {
            i.setManaged(false);
            i.setVisible(false);
        }
    }


    public void logout(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("authentication-view.fxml"));
        Parent root = loader.load();
        Scene currentScene = ((Node) event.getSource()).getScene();
        Stage stage = (Stage) currentScene.getWindow();
        stage.setScene(new Scene(root, currentScene.getWidth(), currentScene.getHeight()));
        stage.show();
    }

    /******************************* SECCIÓN DE DOCTORES *****************************************/

    private void loadDoctors() {

        allDoctorsContainer.getChildren().clear();

        Runnable refresh = () -> {
            Platform.runLater(() -> allDoctorsContainer.getChildren().clear());
            for (Doctor i : doctors) {
                DoctorDisplay d = new DoctorDisplay(i);
                addDoctorEvents(d);
                Platform.runLater(() ->
                        allDoctorsContainer.getChildren().add(d));
            }

            Platform.runLater(() -> {
                totalDoctorsLabel.setText(doctors.size() + "");
                pendingDoctorsLabel.setText("0");
            });
        };

        refresh.run();

        /* Listener para cambios futuros */

        doctors.addListener(
                (ListChangeListener<Doctor>)
                        change -> {

                            while (change.next()) {

                                Platform.runLater(() -> {

                                    totalDoctorsLabel.setText(
                                            doctors.size() + ""
                                    );
                                });

                                if (change.wasAdded()) {

                                    for (Doctor i :
                                            change.getAddedSubList()) {

                                        if (i == null) {
                                            System.out.println(
                                                    "Doctor recibido (null)"
                                            );
                                            continue;
                                        }

                                        System.out.println(
                                                "Doctor recibido: "
                                                        + i.getName()
                                        );

                                        DoctorDisplay d =
                                                new DoctorDisplay(i);

                                        addDoctorEvents(d);

                                        Platform.runLater(() ->
                                                allDoctorsContainer
                                                        .getChildren()
                                                        .add(d));
                                    }

                                } else if (change.wasRemoved()) {

                                    for (Doctor i :
                                            change.getRemoved()) {

                                        for (Node j :
                                                allDoctorsContainer
                                                        .getChildren()) {

                                            if (!(j instanceof DoctorDisplay))
                                                continue;

                                            if (((DoctorDisplay) j)
                                                    .displaysDoctor(i)) {

                                                Platform.runLater(() ->
                                                        allDoctorsContainer
                                                                .getChildren()
                                                                .remove(j));

                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        });
    }

    private void addDoctorEvents(DoctorDisplay d) {
        d.getRemoveBtn().setOnAction(event -> {
            AlertUtil.showConfirmationAlert(
                    "Eliminar médico",
                    "¿Está seguro que quiere eliminar la cuenta de este médico?",
                    () -> removeDoctor(d.getDoctorId()));
        });
    }

    private void removeDoctor(String id) {
        Doctor d = doctorDAO.get(id);
        if (d != null) doctorDAO.delete(d);
    }


    public void filterDoctors(ActionEvent actionEvent) {
        String filter = searchDoctorField.getText();

        searchDoctorField.clear();

        // inicia el filtro en otro hilo
        Thread t = new Thread(() -> {
            removeDoctorFilters();
            startDoctorFilter(filter);
        });
        t.start();
    }

    public void clearDoctorFilters(ActionEvent actionEvent) {
        Thread t = new Thread(this::removeDoctorFilters);
        t.start();
    }


    public void startDoctorFilter(String filter) {
        for (Node i: allDoctorsContainer.getChildren()) {
            if (!(i instanceof DoctorDisplay)) continue;
            DoctorDisplay d = (DoctorDisplay) i;

            if (d.getDoctorName().contains(filter) ||
                    d.getDoctorEmail().contains(filter) ||
                    d.getDoctorSpecialization().contains(filter)) {
                continue;
            }

            Platform.runLater(() -> {
                i.setVisible(false);
                i.setManaged(false);
            });
        }
    }

    public void removeDoctorFilters() {
        for (Node i:  allDoctorsContainer.getChildren()) {
            Platform.runLater(() -> {
                i.setVisible(true);
                i.setManaged(true);
            });
        }
    }

    /******************************* SECCIÓN DE DOCTORES *****************************************/

    private void loadPatients() {
        patientsContainer.getChildren().clear();

        Runnable refresh = () -> {
            Platform.runLater(() -> patientsContainer.getChildren().clear());

            for (Patient i : patients) {
                PatientDisplay p = new PatientDisplay(i);
                addPatientEvents(p);
                Platform.runLater(() ->
                        patientsContainer.getChildren().add(p));
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
                                patientsContainer.getChildren().add(p));
                    }

                } else if (change.wasRemoved()) {

                    for (Patient i: change.getRemoved()) {
                        for (Node j: patientsContainer.getChildren()) {
                            if (!(j instanceof PatientDisplay)) continue;
                            if ( ((PatientDisplay) j).displaysPatient(i) ) {
                                Platform.runLater(() ->
                                        patientsContainer.getChildren().remove(j));
                                break;
                            }
                        }
                    }

                }
            }
        });
    }

    private void addPatientEvents(PatientDisplay p) {
        p.getCommentBtn().setVisible(false);
        p.getCommentBtn().setManaged(false);

        p.getVisualizeBtn().setManaged(false);
        p.getVisualizeBtn().setVisible(false);

        p.getRemoveBtn().setText(" Eliminar cuenta");

        p.getRemoveBtn().setOnAction(event -> {
            AlertUtil.showConfirmationAlert(
                    "Eliminar paciente",
                    "¿Está seguro que quiere eliminar la cuenta de este paciente?",
                    () -> removePatient(p.getPatientId()));
        });
    }

    private void removePatient(String id) {
        Patient p = patientDAO.get(id);

        if (p != null) patientDAO.delete(p);
    }

    public void filterPatients(ActionEvent actionEvent) {
        String filter = searchPatientField.getText();
        searchPatientField.clear();

        removeFilters();

        //inicia el filtro en otro hilo para no congelar el de javafx
        Thread t = new Thread(() -> startFilter(filter));
        t.start();
    }

    public void clearPatientFilters(ActionEvent actionEvent) {
        Thread t = new Thread(() -> removeFilters());
        t.start();
    }


    public void startFilter(String filter) {
        for (Node i: patientsContainer.getChildren()) {
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

    public void removeFilters() {
        for (Node i: patientsContainer.getChildren()) {
            Platform.runLater(() -> {
                i.setVisible(true);
                i.setManaged(true);
            });
        }
    }
}
