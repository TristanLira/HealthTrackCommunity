package com.example.healthtrackcommunity.controls;

import com.example.healthtrackcommunity.models.Prescription;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class PrescriptionDisplay extends HBox {

    private static final int spacing = 5;

    private final Prescription prescription;

    private Label name;
    private Label frequency;
    private Label instructions;

    private Separator separator;

    private Button removeBtn;
    private FontIcon removeBtnIcon;

    public PrescriptionDisplay(Prescription prescription) {
        super();

        this.prescription = prescription;

        name = new Label(prescription.getName());
        frequency = new Label(prescription.getFrequency());
        instructions = new Label(prescription.getInstructions());

        build();
        addCss();
    }

    private void build() {

        instructions.setWrapText(true);

        removeBtn = new Button(" Eliminar");
        removeBtnIcon = new FontIcon("fas-trash-alt");
        removeBtn.setGraphic(removeBtnIcon);

        separator = new Separator();

        VBox prescriptionInfo = new VBox(8);

        //medicamento y botón de eliminar
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(name, spacer, removeBtn);

        //frecuencia
        VBox frequencyBox = new VBox(spacing);
        Label frequencyTitle = new Label("Frecuencia");
        frequencyTitle.getStyleClass().add("prescription-label");
        frequencyBox.getChildren().addAll(frequencyTitle, frequency);

        //instrucciones
        VBox instructionsBox = new VBox(spacing);
        Label instructionsTitle = new Label("Indicaciones");
        instructionsTitle.getStyleClass().add("prescription-label");
        instructionsBox.getChildren().addAll(instructionsTitle, instructions);

        prescriptionInfo.getChildren().addAll(
                header,
                separator,
                frequencyBox,
                instructionsBox
        );

        this.getChildren().add(prescriptionInfo);

        HBox.setHgrow(prescriptionInfo, Priority.ALWAYS);
    }

    private void addCss() {
        this.getStyleClass().add("prescription-display");
        name.getStyleClass().add("prescription-name");
        frequency.getStyleClass().add("prescription-value");
        instructions.getStyleClass().add("prescription-instructions");

        removeBtn.getStyleClass().add("prescription-remove-btn");
        removeBtnIcon.getStyleClass().add("prescription-remove-icon");
        separator.getStyleClass().add("prescription-separator");
    }

    public boolean displaysPrescription(Prescription p) {
        return p.getId().equals(prescription.getId());
    }

    public String getPrescriptionId() {
        return prescription.getId();
    }

    public Button getRemoveBtn() {
        return removeBtn;
    }

    public void hideRemoveButton() {
        removeBtn.setVisible(false);
        removeBtn.setManaged(false);
    }
}