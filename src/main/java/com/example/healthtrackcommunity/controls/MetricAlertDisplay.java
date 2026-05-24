package com.example.healthtrackcommunity.controls;

import com.example.healthtrackcommunity.models.MetricAlert;
import com.example.healthtrackcommunity.models.Patient;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class MetricAlertDisplay extends HBox {

    private final int spacing = 5;

    private final MetricAlert alert;
    private final Patient patient;

    private Label patientNameLabel;
    private Label dateLabel;
    private Label totalDangerLabel;

    private VBox dangerMetricsBox;

    private Button commentBtn;
    private FontIcon commentBtnIcon;

    private Button dismissBtn;
    private FontIcon dismissBtnIcon;

    public MetricAlertDisplay(MetricAlert alert, Patient patient) {
        super();

        this.alert = alert;
        this.patient = patient;

        build();
        addCss();
    }

    private void build() {

        patientNameLabel = new Label(patient.getName());
        dateLabel = new Label(alert.getDate() + " " + alert.getTimeObj().getHour() + ":" + String.format("%02d", alert.getTimeObj().getMinute()));
        totalDangerLabel = new Label("Tendencias peligrosas: " + alert.getAllDangerousTendencies());

        // métricas
        dangerMetricsBox = new VBox();
        dangerMetricsBox.setSpacing(spacing);

        addDangerMetric("Presión arterial", alert.getDangerousPressureCounter());
        addDangerMetric("Glucosa", alert.getDangerousGlucoseCounter());
        addDangerMetric("Frecuencia cardiaca", alert.getDangerousHeartRateCounter());
        addDangerMetric("Peso / IMC", alert.getDangerousWeightCounter());

        // botones
        commentBtn = new Button(" Comentar");
        commentBtnIcon = new FontIcon("fas-comment-medical");
        commentBtn.setGraphic(commentBtnIcon);

        dismissBtn = new Button(" Descartar");
        dismissBtnIcon = new FontIcon("fas-times-circle");
        dismissBtn.setGraphic(dismissBtnIcon);

        VBox buttonsBox = new VBox();
        buttonsBox.setSpacing(spacing * 2);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.getChildren().addAll(commentBtn, dismissBtn);

        // columna izquierda
        VBox leftColumn = new VBox();
        leftColumn.setSpacing(spacing * 2);
        leftColumn.getChildren().addAll(patientNameLabel, dateLabel, totalDangerLabel);

        // espaciador
        Region spacer1 = new Region();
        Region spacer2 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        // layout
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(spacing * 3);
        getChildren().addAll(leftColumn, spacer1, dangerMetricsBox, spacer2, buttonsBox);
    }

    private void addDangerMetric(String metricName, int counter) {
        if (counter <= 0) return;

        Label metricLabel = new Label(metricName + ": " + counter);
        metricLabel.setMaxWidth(Double.MAX_VALUE);

        metricLabel.getStyleClass().add("danger-metric");

        dangerMetricsBox.getChildren().add(metricLabel);
    }

    private void addCss() {
        getStyleClass().add("metric-alert-display");

        patientNameLabel.getStyleClass().add("metric-alert-patient");

        dateLabel.getStyleClass().add("metric-alert-date");

        totalDangerLabel.getStyleClass().add("metric-alert-total");

        commentBtn.getStyleClass().add("comment-alert-btn");
        commentBtnIcon.getStyleClass().add("comment-alert-icon");

        dismissBtn.getStyleClass().add("dismiss-alert-btn");
        dismissBtnIcon.getStyleClass().add("dismiss-alert-icon");
    }

    public boolean displaysAlert(MetricAlert a) {
        if (a.getId() == null || a.getId().isEmpty()) return false;

        return alert.getId().equals(a.getId());
    }

    public String getAlertId() {
        return alert.getId();
    }

    public String getPatientId() {
        return alert.getPatientId();
    }

    public MetricAlert getAlert() {
        return alert;
    }

    public Button getCommentBtn() {
        return commentBtn;
    }

    public Button getDismissBtn() {
        return dismissBtn;
    }
}