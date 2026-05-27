package com.example.healthtrackcommunity.controls;

import com.example.healthtrackcommunity.models.Doctor;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class DoctorDisplay extends HBox {

    private final int spacing = 5;

    private Doctor d;

    private Label name;
    private Label email;
    private Label specialization;

    private Button removeBtn;
    private FontIcon removeBtnIcon;

    public DoctorDisplay(Doctor d) {
        super();

        this.d = d;

        build();
        addCss();
    }

    private void build() {

        name = new Label(d.getName());
        email = new Label(d.getEmail());
        specialization =
                new Label(d.getSpecialization());

        removeBtn =
                new Button(" Eliminar cuenta");

        removeBtnIcon =
                new FontIcon("fas-user-minus");

        removeBtn.setGraphic(
                removeBtnIcon
        );

        VBox v1 = new VBox();

        v1.setSpacing(spacing);

        v1.getChildren().addAll(
                name,
                email,
                specialization
        );


        HBox h1 = new HBox();

        h1.setAlignment(Pos.CENTER);

        h1.getChildren().add(
                removeBtn
        );

        Region spacing1 =
                new Region();

        HBox.setHgrow(
                spacing1,
                Priority.ALWAYS
        );

        this.setAlignment(
                Pos.CENTER_LEFT
        );

        this.getChildren().addAll(
                v1,
                spacing1,
                h1
        );
    }

    private void addCss() {

        this.getStyleClass()
                .add("patient-display");

        name.getStyleClass()
                .add("patient-name");

        email.getStyleClass()
                .add("patient-email");

        specialization.getStyleClass()
                .add("patient-email");

        removeBtn.getStyleClass()
                .add("remove-patient-btn");

        removeBtnIcon.getStyleClass()
                .add("remove-patient-icon");
    }

    public boolean displaysDoctor(
            Doctor d2
    ) {

        if (d2.getId() == null
                || d2.getId().isEmpty()) {

            return false;
        }

        return d.getId()
                .equals(d2.getId());
    }

    public String getDoctorId() {
        return d.getId();
    }

    public Button getRemoveBtn() {
        return removeBtn;
    }

    public String getDoctorName() {
        return d.getName();
    }

    public String getDoctorEmail() {
        return d.getEmail();
    }

    public String getDoctorSpecialization() {
        return d.getSpecialization();
    }
}
