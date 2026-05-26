package com.example.healthtrackcommunity;

import com.example.healthtrackcommunity.models.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class ChartGenerator {

    private TabPane tab;

    private VBox pressureContainer;
    private VBox glucoseContainer;
    private VBox heartRateContainer;
    private VBox weightContainer;

    private ObservableList<Metric> recent;

    public ChartGenerator(VBox pressureContainer,
                          VBox glucoseContainer,
                          VBox heartRateContainer,
                          VBox weightContainer,
                          ObservableList<Metric> recent) {
        this.pressureContainer = pressureContainer;
        this.glucoseContainer = glucoseContainer;
        this.heartRateContainer = heartRateContainer;
        this.weightContainer = weightContainer;
        this.recent = recent;
    }

    public void setTab(TabPane tab) {
        this.tab = tab;
    }

    public void generateMetricsCharts() {
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
            pressureContainer.getChildren().clear();
            pressureContainer.getChildren().add(pressure);

            glucoseContainer.getChildren().clear();
            glucoseContainer.getChildren().add(glucose);

            heartRateContainer.getChildren().clear();
            heartRateContainer.getChildren().add(heartRate);

            weightContainer.getChildren().clear();
            weightContainer.getChildren().add(bmi);

            reloadTab();
        });
    }

    private void reloadTab() {
        if (tab == null) return;
        Tab selected = tab.getSelectionModel().getSelectedItem();
        tab.getSelectionModel().clearSelection();
        tab.getSelectionModel().select(selected);
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
