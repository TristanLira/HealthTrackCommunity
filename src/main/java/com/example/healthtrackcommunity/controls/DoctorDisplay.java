package com.example.healthtrackcommunity.controls;

import com.example.healthtrackcommunity.models.Doctor;
import com.example.healthtrackcommunity.models.DoctorAccountRequest;
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
    private DoctorAccountRequest r;

    private Label name;
    private Label email;
    private Label specialization;

    private Button removeBtn;
    private FontIcon removeBtnIcon;

    private Button acceptBtn;
    private FontIcon acceptBtnIcon;

    public DoctorDisplay(Doctor d) {
        super();

        this.d = d;

        name = new Label(d.getName());
        email = new Label(d.getEmail());
        specialization = new Label(d.getSpecialization());

        build();
        addCss();
    }

    public DoctorDisplay(DoctorAccountRequest r) {
        super();

        this.r = r;

        name = new Label(r.getName());
        email = new Label(r.getEmail());
        specialization = new Label(r.getSpecialization());

        build();
        addCss();
    }

    private void build() {

        removeBtn = new Button(" Eliminar cuenta");
        removeBtnIcon = new FontIcon("fas-user-minus");
        removeBtn.setGraphic(removeBtnIcon);

        acceptBtn = new Button(" Crear cuenta");
        acceptBtnIcon = new FontIcon("fas-user-plus");
        acceptBtn.setGraphic(acceptBtnIcon);

        VBox v1 = new VBox();

        v1.setSpacing(spacing);

        v1.getChildren().addAll(name, email, specialization);

        HBox h1 = new HBox();
        h1.setSpacing(spacing * 2);
        h1.setAlignment(Pos.CENTER);
        if (d != null) {
            h1.getChildren().add(removeBtn);
        } else {
            removeBtn.setText(" Eliminar solicitud");
            h1.getChildren().addAll(acceptBtn, removeBtn);
        }

        Region spacing1 = new Region();

        HBox.setHgrow(spacing1, Priority.ALWAYS);

        this.setAlignment(Pos.CENTER_LEFT);
        this.getChildren().addAll(v1, spacing1, h1);
    }

    private void addCss() {

        this.getStyleClass().add("patient-display");
        name.getStyleClass().add("patient-name");
        email.getStyleClass().add("patient-email");
        specialization.getStyleClass().add("patient-email");

        removeBtn.getStyleClass().add("remove-patient-btn");
        removeBtnIcon.getStyleClass().add("remove-patient-icon");

        acceptBtn.getStyleClass().add("comment-btn");
        acceptBtnIcon.getStyleClass().add("comment-btn-icon");
    }

    public boolean displaysDoctor(Doctor d2) {
        if (d == null) return false;
        if (d2.getId() == null || d2.getId().isEmpty()) return false;
        return d.getId().equals(d2.getId());
    }

    public boolean displaysDoctorRequest(DoctorAccountRequest r2) {
        if (r == null) return false;
        if (r2.getId() == null || r2.getId().isEmpty()) return false;
        return r.getId().equals(r2.getId());
    }

    public String getDoctorId() {
        return d == null ? "" : d.getId();
    }

    public String getRequestId() {
        return r == null ? "": r.getId();
    }

    public Button getRemoveBtn() {
        return removeBtn;
    }

    public Button getAcceptBtn() {
        return acceptBtn;
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
