package com.example.healthtrackcommunity.controls;

import com.example.healthtrackcommunity.models.PressureMetric;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class PressureDisplay extends MetricDisplay {

    private final Label systolic;
    private final Label diastolic;
    private final Label systolicTitle;
    private final Label diastolicTitle;

    public PressureDisplay(PressureMetric p) {
        super(p);

        metricTitle.setText("Presión arterial");

        systolic = new Label(p.getSystolic() + " mm Hg");
        diastolic = new Label(p.getDiastolic() + " mm Hg");
        systolicTitle = new Label("Presión sistólica:");
        diastolicTitle = new Label("Presión diastólica:");

        GridPane grid = new GridPane();
        grid.add(systolicTitle, 0, 0);
        grid.add(diastolicTitle, 0, 1);
        grid.add(systolic, 1, 0);
        grid.add(diastolic, 1, 1);

        grid.setVgap(spacing);
        grid.setHgap(spacing);

        metric.getChildren().add(grid);

        addCss();
    }

    @Override
    protected void addCss() {
        super.addCss();

        this.getStyleClass().add("metric-card-pressure");

        systolic.getStyleClass().add("metric-field-value");
        diastolic.getStyleClass().add("metric-field-value");
        systolicTitle.getStyleClass().add("metric-field-title");
        diastolicTitle.getStyleClass().add("metric-field-title");
    }
}
