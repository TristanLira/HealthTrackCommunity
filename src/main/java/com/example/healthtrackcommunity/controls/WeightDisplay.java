package com.example.healthtrackcommunity.controls;

import com.example.healthtrackcommunity.models.WeightMetric;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class WeightDisplay extends MetricDisplay {

    private final Label weight;
    private final Label bmi;
    private final Label weightTitle;
    private final Label bmiTitle;

    public WeightDisplay(WeightMetric w) {
        super(w);

        metricTitle.setText("Indice de masa corporal");

        weight = new Label(w.getWeight() + " kg");
        bmi = new Label(String.format("%.2f", w.getBmi()));

        weightTitle = new Label("Peso:");
        bmiTitle = new Label("IMC:");

        GridPane grid = new GridPane();
        grid.add(weightTitle, 0, 0);
        grid.add(bmiTitle, 0, 1);
        grid.add(weight, 1, 0);
        grid.add(bmi, 1, 1);

        grid.setVgap(spacing);
        grid.setHgap(spacing);

        metric.getChildren().add(grid);

        addCss();
    }

    @Override
    protected void addCss() {
        super.addCss();

        this.getStyleClass().add("metric-card-weight");

        weight.getStyleClass().add("metric-field-value");
        bmi.getStyleClass().add("metric-field-value");
        weightTitle.getStyleClass().add("metric-field-title");
        bmiTitle.getStyleClass().add("metric-field-title");

    }
}
