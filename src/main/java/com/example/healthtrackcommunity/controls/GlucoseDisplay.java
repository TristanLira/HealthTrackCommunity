package com.example.healthtrackcommunity.controls;

import com.example.healthtrackcommunity.models.GlucoseMetric;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class GlucoseDisplay extends MetricDisplay {

    private Label glucose;
    private Label glucoseTitle;

    public GlucoseDisplay(GlucoseMetric g) {
        super(g);

        metricTitle.setText("Glucosa en sangre");

        glucose = new Label( g.getGlucose() + " mg/dL");
        glucoseTitle = new Label("Glucosa:");

        GridPane grid = new GridPane();
        grid.add(glucoseTitle, 0, 0);
        grid.add(glucose, 1, 0);

        grid.setVgap(spacing);
        grid.setHgap(spacing);

        metric.getChildren().add(grid);

        addCss();
    }

    @Override
    protected void addCss() {
        super.addCss();

        this.getStyleClass().add("metric-card-glucose");

        glucose.getStyleClass().add("metric-field-value");
        glucoseTitle.getStyleClass().add("metric-field-title");
    }
}
