package com.example.healthtrackcommunity.controls;

import com.example.healthtrackcommunity.models.HeartRateMetric;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class HeartRateDisplay extends MetricDisplay {

    private Label heartRate;
    private Label heartRateTitle;

    public HeartRateDisplay(HeartRateMetric h) {
        super(h);

        metricTitle.setText("Frecuencia cardíaca");

        heartRate = new Label( h.getHeartRate() + " ppm");
        heartRateTitle = new Label("Frecuencia:");

        GridPane grid = new GridPane();
        grid.add(heartRateTitle, 0, 0);
        grid.add(heartRate, 1, 0);

        grid.setVgap(spacing);
        grid.setHgap(spacing);

        metric.getChildren().add(grid);

        addCss();
    }

    @Override
    protected void addCss() {
        super.addCss();

        this.getStyleClass().add("metric-card-heart-rate");

        heartRate.getStyleClass().add("metric-field-value");
        heartRateTitle.getStyleClass().add("metric-field-title");
    }
}
