package com.example.healthtrackcommunity;

import com.example.healthtrackcommunity.controls.*;
import com.example.healthtrackcommunity.models.*;
import config.FamilyMemberDAO;
import config.MetricDAO;
import config.PatientDAO;
import config.RecentMetrics;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;

public class FamilyController {

    private static final Logger log = LoggerFactory.getLogger(FamilyController.class);
    public Label familyNameLabel;
    public ScrollPane mainScrollPane;
    public StackPane mainContent;
    
    //dashboard
    public VBox dashboardSection;
    public Label patientCountLabel;
    public Label lastAlertLabel;
    public VBox alertsContainer;
    public TextField patientEmailField;
    public PasswordField patientPasswordField;
    
    //pacientes
    public VBox patientsListSection;
    public TextField searchPatientField;
    public VBox patientsContainer;
    
    //métricas
    public VBox metricsSection;
    public ComboBox<Patient> patientSelector;
    public TabPane metricsTabPane;
    public VBox pressureMetricsContainer;
    public VBox glucoseMetricsContainer;
    public VBox heartRateMetricsContainer;
    public VBox weightMetricsContainer;

    //gráficos
    public VBox chartsSection;
    public ComboBox<Patient> patientChartSelector;
    public TabPane chartsTabPane;
    public VBox pressureChartContainer;
    public VBox glucoseChartContainer;
    public VBox heartRateChartContainer;
    public VBox weightChartContainer;

    private FamilyMemberDAO familyDAO;
    private FamilyMember logged;
    private ObservableList<Patient> familyPatients;
    private ObservableList<Metric> recent;

    private PatientDAO patientDAO;

    public void initialize() {}

    public void setLoggedUser(FamilyMemberDAO familyDAO, FamilyMember logged, PatientDAO patientDA0) {
        this.familyDAO = familyDAO;
        this.logged = logged;
        familyNameLabel.setText(logged.getName());

        this.patientDAO = patientDA0;

        createFamilyPatientsList();
        loadPatients();
    }

    /******************************* MOSTRAR SECCIONES *****************************************/

    public void showDashboard(ActionEvent actionEvent) {
        hideAllSections();
        dashboardSection.setManaged(true);
        dashboardSection.setVisible(true);
    }

    public void showPatientsList(ActionEvent actionEvent) {
        hideAllSections();
        patientsListSection.setManaged(true);
        patientsListSection.setVisible(true);
    }

    public void showMetrics(ActionEvent actionEvent) {
        hideAllSections();
        metricsSection.setManaged(true);
        metricsSection.setVisible(true);
    }

    public void showCharts(ActionEvent actionEvent) {
        hideAllSections();
        chartsSection.setManaged(true);
        chartsSection.setVisible(true);
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

    private void reloadTab(TabPane pane) {
        Tab selected = pane.getSelectionModel().getSelectedItem();
        pane.getSelectionModel().clearSelection();
        pane.getSelectionModel().select(selected);
    }

    /******************************* SECCIÓN DE DASHBOARD *****************************************/
    public void addPatient(ActionEvent actionEvent) {
        String patientEmail = patientEmailField.getText();
        String patientPassword = patientPasswordField.getText();

        cleanPatientForm();

        //hace la búsqueda en otro hilo para no bloquear el hilo de javafx
        Thread t = new Thread(() -> {
            Patient patient = null;
            for (Patient i: patientDAO.getAll()) {
                if (i.getEmail().equals(patientEmail) && i.getPassword().equals(patientPassword)) {
                    patient = i;
                    break;
                }
            }

            if (patient == null) {
                Platform.runLater(() ->
                    AlertUtil.showErrorAlert("Paciente no encontrado", "No se encontró el paciente, verifique el email y la contraseña."));
                return;
            }

            //si ya tiene el paciente evita registrarlo de nuevo
            if (logged.getPatientsId().contains(patient.getId())) {
                Platform.runLater(() ->
                    AlertUtil.showErrorAlert("Paciente registrado", "Ya ha registrado a este paciente."));
                return;
            }

            familyPatients.add(patient);
            logged.addPatientId(patient.getId());
            //familyDAO.update(logged);

            familyDAO.update(logged,
                    () -> Platform.runLater(() ->
                            AlertUtil.showInfoAlert("Paciente agregado", "El paciente fue agregado correctamente a su cuenta.")),
                    () -> Platform.runLater(() ->
                            AlertUtil.showErrorAlert("Error", "El paciente no pudo ser agregado, inténtelo de nuevo."))
            );
        });

        t.start();
    }

    private void cleanPatientForm() {
        patientEmailField.clear();
        patientPasswordField.clear();
    }

    private void createFamilyPatientsList() {
        familyPatients = FXCollections.observableArrayList();
        patientSelector.setItems(familyPatients);
        patientChartSelector.setItems(familyPatients);

        for (String i: logged.getPatientsId()) {
            Patient p = patientDAO.get(i);
            if (p != null) familyPatients.add(p);
        }
    }

    /******************************* SECCIÓN DE PACIENTES *****************************************/

    private void loadPatients() {
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

        patientsContainer.getChildren().clear();

        Runnable refresh = () -> {
            Platform.runLater(() -> patientsContainer.getChildren().clear());

            for (Patient i : familyPatients) {
                PatientDisplay p = new PatientDisplay(i);
                addPatientEvents(p);
                Platform.runLater(() ->
                        patientsContainer.getChildren().add(p));
            }

            Platform.runLater(() ->
                    patientCountLabel.setText(familyPatients.size() + ""));
        };

        refresh.run();

        //evento para los futuros pacientes agregados
        familyPatients.addListener((ListChangeListener<Patient>) change -> {
            while(change.next()) {
                Platform.runLater(() ->
                        patientCountLabel.setText(familyPatients.size() + ""));

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
        //como reutiliza el display de doctor, desactiva algunas de las funciones disponibles para el doctor
        p.getCommentBtn().setManaged(false);
        p.getCommentBtn().setVisible(false);
        p.getRemoveBtn().setManaged(false);
        p.getRemoveBtn().setVisible(false);
        p.getVisualizeBtn().setManaged(false);
        p.getVisualizeBtn().setVisible(false);
    }

    public void filterPatients(ActionEvent actionEvent) {
        String filter = searchPatientField.getText();
        searchPatientField.clear();

        removeFilters();

        //inicia el filtro en otro hilo para no congelar el de javafx
        Thread t = new Thread(() -> startFilter(filter));
        t.start();
    }

    public void clearFilter(ActionEvent actionEvent) {
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

    /******************************* SECCIÓN DE MÉTRICAS *****************************************/

    public void onPatientSelected(ActionEvent actionEvent) {
        if (patientSelector.getValue() != null) {
            loadMetricDisplays(patientSelector.getValue());
        }
    }

    private void loadMetricDisplays(Patient p) {
        MetricDAO pressureDAO = new MetricDAO(p, MetricDAO.PRESSURE);
        MetricDAO glucoseDAO = new MetricDAO(p, MetricDAO.GLUCOSE);
        MetricDAO heartRateDAO = new MetricDAO(p, MetricDAO.HEART_RATE);
        MetricDAO weightDAO = new MetricDAO(p, MetricDAO.WEIGHT);

        loadDisplay(pressureDAO.getAll(), pressureMetricsContainer, PressureMetric.class);
        loadDisplay(heartRateDAO.getAll(), heartRateMetricsContainer, HeartRateMetric.class);
        loadDisplay(glucoseDAO.getAll(), glucoseMetricsContainer, GlucoseMetric.class);
        loadDisplay(weightDAO.getAll(), weightMetricsContainer, WeightMetric.class);
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

                reloadTab(metricsTabPane);
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


    /******************************* SECCIÓN DE GRÁFICOS *****************************************/

    public void onPatientChartSelected(ActionEvent actionEvent) {
        if (patientChartSelector.getValue() == null) return;
        recent = new RecentMetrics(patientChartSelector.getValue()).getRecent();

        recent.addListener((ListChangeListener<? super Metric>) change -> {
            while (change.next()) generateMetricsCharts();
        });
    }

    private void generateMetricsCharts() {
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
            pressureChartContainer.getChildren().clear();
            pressureChartContainer.getChildren().add(pressure);

            glucoseChartContainer.getChildren().clear();
            glucoseChartContainer.getChildren().add(glucose);

            heartRateChartContainer.getChildren().clear();
            heartRateChartContainer.getChildren().add(heartRate);

            weightChartContainer.getChildren().clear();
            weightChartContainer.getChildren().add(bmi);

            reloadTab(chartsTabPane);
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
}
