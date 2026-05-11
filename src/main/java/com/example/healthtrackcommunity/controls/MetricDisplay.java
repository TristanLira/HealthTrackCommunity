package com.example.healthtrackcommunity.controls;

import com.example.healthtrackcommunity.models.Metric;
import com.example.healthtrackcommunity.models.PressureMetric;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MetricDisplay extends GridPane {

    private final String metricId;

    protected Label metricTitle;
    protected VBox metric;

    private Label dateTitle;
    private Label date;

    private Label timeTitle;
    private Label time;

    public MetricDisplay(Metric m) {
        super();

        metricId = m.getId();

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

        date.setText(m.getDateObj().format(dateFormat));
        time.setText(m.getTimeObj().format(timeFormat));

        metricTitle.setText("Métrica");
        dateTitle.setText("Fecha");
        timeTitle.setText("Hora");

        addCss();
    }

    private void build() {
        VBox v1 = new VBox();
        v1.setSpacing(5);
        v1.getChildren().addAll(metricTitle, metric);

        VBox v2 = new VBox();
        v1.setSpacing(5);
        v2.getChildren().addAll(dateTitle, date);

        VBox v3 = new VBox();
        v1.setSpacing(5);
        v2.getChildren().addAll(timeTitle, time);

        this.add(v1, 0, 0);
        this.add(v2, 0, 1);
        this.add(v3, 0, 2);
    }

    protected void addCss() {

    }

    public String getMetricId() {
        return metricId;
    }
}
