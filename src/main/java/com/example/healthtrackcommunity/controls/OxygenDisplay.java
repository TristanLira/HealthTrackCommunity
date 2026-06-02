package com.example.healthtrackcommunity.controls;

import com.example.healthtrackcommunity.models.OxygenMetric;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class OxygenDisplay extends MetricDisplay {

    private Label oxygen;
    private Label oxygenTitle;

    public OxygenDisplay(OxygenMetric o) {
        super(o);

        metricTitle.setText("Saturación de oxígeno en sangre");

        oxygen = new Label(o.getOxygen() + "%");
        oxygenTitle = new Label("Saturación: ");

        GridPane grid = new GridPane();
        grid.add(oxygenTitle, 0, 0);
        grid.add(oxygen, 1, 0);

        grid.setVgap(spacing);
        grid.setHgap(spacing);

        metric.getChildren().add(grid);

        addCss();
    }

    @Override
    protected void addCss() {
        super.addCss();

        this.getStyleClass().add("metric-card-oxygen");

        oxygen.getStyleClass().add("metric-field-value");
        oxygenTitle.getStyleClass().add("metric-field-title");
    }
}
