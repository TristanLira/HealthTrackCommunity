package com.example.healthtrackcommunity.controls;

import com.example.healthtrackcommunity.models.Metric;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MetricDisplay extends VBox {

    Metric m;

    protected final int spacing = 10;

    protected Label metricTitle;
    protected VBox metric;

    private Label dateTitle;
    private Label date;

    private Label timeTitle;
    private Label time;

    public MetricDisplay(Metric m) {
        //super();

        this.m = m;

        metricTitle = new Label();
        metric = new VBox();
        dateTitle = new Label();
        date = new Label();
        timeTitle = new Label();
        time = new Label();


        //agregar los datos

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

        date.setText(m.getDateObj().format(dateFormat));
        time.setText(m.getTimeObj().format(timeFormat));

        metricTitle.setText("Métrica");
        dateTitle.setText("Fecha de registro");
        timeTitle.setText("Hora de registro");

        build();
    }

    private void build() {
        HBox content = new HBox();

        content.setPadding(new Insets(spacing,spacing,spacing,spacing));

        content.setAlignment(Pos.CENTER);
        content.maxWidth(Double.MAX_VALUE);
        HBox.setHgrow(content, Priority.ALWAYS);

        this.setAlignment(Pos.CENTER_LEFT);
        this.maxWidth(Double.MAX_VALUE);

        VBox v1 = new VBox();
        v1.setSpacing(spacing);
        v1.getChildren().add(metric);

        VBox v2 = new VBox();
        v2.setSpacing(spacing);
        v2.getChildren().addAll(dateTitle, date);

        VBox v3 = new VBox();
        v3.setSpacing(spacing);
        v3.getChildren().addAll(timeTitle, time);

        Region spacing1 = new Region();
        Region spacing2 = new Region();

        HBox.setHgrow(spacing1, Priority.ALWAYS);
        HBox.setHgrow(spacing2, Priority.ALWAYS);

        content.getChildren().addAll(v1, spacing1, v2, spacing2, v3);

        this.getChildren().addAll(metricTitle, content);
    }

    protected void addCss() {
        this.getStyleClass().add("metric-card");

        metricTitle.getStyleClass().add("metric-title");

        dateTitle.getStyleClass().add("metric-field-title");
        timeTitle.getStyleClass().add("metric-field-title");

        date.getStyleClass().add("metric-field-value");
        time.getStyleClass().add("metric-field-value");

        //metric.getStyleClass().add("metric-content");
    }

    public void hideTitle() {
        metricTitle.setVisible(false);
        metricTitle.setManaged(false);
    }

    public boolean isMetric(Metric m2) {
        if (m.getClass() != m2.getClass()) return false;

        if (m.getId() == null || m.getId().isEmpty()) return false;

        return m.getId().equals(m2.getId());
    }
}
