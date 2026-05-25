package com.example.healthtrackcommunity.controls;

import com.example.healthtrackcommunity.models.Patient;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.kordamp.ikonli.javafx.FontIcon;

public class PatientDisplay extends HBox {

    private final int spacing = 5;

    private Patient p;

    private Label name;
    private Label email;

    private Button removeBtn;
    private FontIcon removeBtnIcon;

    private Button visualizeBtn;
    private FontIcon visualizeBtnIcon;

    private Button commentBtn;
    private FontIcon commentBtnIcon;


    public PatientDisplay(Patient p) {
        super();

        this.p = p;

        build();
        addCss();
    }

    private void build() {
        name = new Label(p.getName());
        email = new Label(p.getEmail());

        removeBtn = new Button(" Eliminar");
        removeBtnIcon = new FontIcon("fas-user-minus");
        removeBtn.setGraphic(removeBtnIcon);

        visualizeBtn = new Button(" Visualizar");
        visualizeBtnIcon = new FontIcon("fas-user");
        visualizeBtn.setGraphic(visualizeBtnIcon);

        commentBtn = new Button(" Enviar comentario");
        commentBtnIcon = new FontIcon("fas-comment");
        commentBtn.setGraphic(commentBtnIcon);

        //primera columna
        VBox v1 = new VBox();
        v1.setSpacing(spacing);
        v1.getChildren().addAll(name, email);

        //segunda columna
        HBox h1 = new HBox();
        h1.setAlignment(Pos.CENTER);
        h1.setSpacing(spacing * 2);
        h1.getChildren().addAll(commentBtn, visualizeBtn, removeBtn);

        //espacios
        Region spacing1 = new Region();
        HBox.setHgrow(spacing1, Priority.ALWAYS);

        this.setAlignment(Pos.CENTER_LEFT);
        this.getChildren().addAll(v1, spacing1, h1);
    }

    private void addCss() {
        this.getStyleClass().add("patient-display");

        name.getStyleClass().add("patient-name");
        email.getStyleClass().add("patient-email");

        removeBtn.getStyleClass().add("remove-patient-btn");
        removeBtnIcon.getStyleClass().add("remove-patient-icon");

        visualizeBtn.getStyleClass().add("accept-request-btn");
        visualizeBtnIcon.getStyleClass().add("accept-request-icon");

        commentBtn.getStyleClass().add("comment-btn");
        commentBtnIcon.getStyleClass().add("comment-btn-icon");
    }

    public boolean displaysPatient(Patient p2) {
        if (p2.getId() == null || p2.getId().isEmpty()) return false;
        return p.getId().equals(p2.getId());
    }

    public String getPatientId() {
        return p.getId();
    }

    public Button getRemoveBtn() {
        return removeBtn;
    }

    public Button getVisualizeBtn() {
        return visualizeBtn;
    }

    public Button getCommentBtn() {
        return commentBtn;
    }

    public String getPatientName() {
        return p.getName();
    }

    public String getPatientEmail() {
        return p.getEmail();
    }

}
