package com.example.healthtrackcommunity;

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
    public VBox allPatientsContainer;

    public void setLoggedUser() {

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

    public void filterDoctors(ActionEvent actionEvent) {
    }

    public void clearDoctorFilters(ActionEvent actionEvent) {
    }

    /******************************* SECCIÓN DE DOCTORES *****************************************/
    public void filterPatients(ActionEvent actionEvent) {
    }

    public void clearPatientFilters(ActionEvent actionEvent) {
    }
}
