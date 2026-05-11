package com.example.healthtrackcommunity;

import com.example.healthtrackcommunity.models.*;
import config.MetricDAO;
import config.PatientDAO;
import config.DoctorDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;

public class PatientController {

    @FXML public Label patientNameLabel;
    @FXML public Label assignedDoctorLabel;
    @FXML public Button requestDoctorBtn;
    @FXML public Label requestStatusLabel;
    @FXML public VBox dashboardSection;
    @FXML public VBox noDoctorWarning;
    @FXML public Label healthAlertLabel;
    @FXML public VBox recentMetricsContainer;
    @FXML public Label nextReminderLabel;
    @FXML public VBox historySection;
    @FXML public VBox historyPressureContainer;
    @FXML public VBox historyGlucoseContainer;
    @FXML public VBox historyHeartRateContainer;
    @FXML public VBox historyWeightContainer;
    @FXML public VBox chartsSection;
    @FXML public VBox patientGlucoseChart;
    @FXML public VBox patientPressureChart;
    @FXML public VBox patientHeartRateChart;
    @FXML public VBox patientWeightChart;
    @FXML public ScrollPane mainScrollPane;
    @FXML public StackPane mainContent;

    private PatientDAO patientDAO;
    private Patient logged;
    private DoctorDAO doctorDAO;
    private MetricDAO heartRateDAO;
    private MetricDAO pressureDAO;
    private MetricDAO glucoseDAO;
    private MetricDAO weightDAO;
    private ObservableList<Metric> heartRateList;
    private ObservableList<Metric> pressureList;
    private ObservableList<Metric> glucoseList;
    private ObservableList<Metric> weightList;
    private String requestStatus = "";

    public void initialize() {
    }

    public void setLoggedUser(PatientDAO dao, Patient logged) {
        this.logged = logged;
        this.patientDAO = dao;
        this.doctorDAO = new DoctorDAO();

        patientNameLabel.setText(logged.getName());
        initDAOs();
    }

    private void initDAOs() {
        heartRateDAO = new MetricDAO(logged, MetricDAO.HEART_RATE);
        pressureDAO = new MetricDAO(logged, MetricDAO.PRESSURE);
        glucoseDAO = new MetricDAO(logged, MetricDAO.GLUCOSE);
        weightDAO = new MetricDAO(logged, MetricDAO.WEIGHT);

        heartRateList = heartRateDAO.getAll();
        pressureList = pressureDAO.getAll();
        glucoseList = glucoseDAO.getAll();
        weightList = weightDAO.getAll();
    }

    /*MOSTRAR SECCIONES*/

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
    }

    @FXML
    public void showCharts(ActionEvent event) {
        hideAllSections();
        chartsSection.setVisible(true);
        chartsSection.setManaged(true);
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
}
