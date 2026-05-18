package com.example.healthtrackcommunity.controls;

import com.example.healthtrackcommunity.models.MonitoringRequest;
import com.example.healthtrackcommunity.models.Patient;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.awt.*;

public class MonitoringRequestDisplay extends HBox {

    private final int spacing = 5;

    private final MonitoringRequest r;
    private final Patient p;

    private Label name;
    private Label email;

    private Button acceptBtn;
    private FontIcon acceptBtnIcon;

    private Button declineBtn;
    private FontIcon declineBtnIcon;

    public MonitoringRequestDisplay(MonitoringRequest r, Patient p) {
        this.r = r;
        this.p = p;

        build();
        addCss();
    }

    private void build() {
        name = new Label(p.getName());
        email = new Label(p.getEmail());

        acceptBtn = new Button(" Aceptar");
        acceptBtnIcon = new FontIcon("fas-user-check");
        acceptBtn.setGraphic(acceptBtnIcon);

        declineBtn = new Button(" Rechazar");
        declineBtnIcon = new FontIcon("fas-user-slash");
        declineBtn.setGraphic(declineBtnIcon);

        //primera columna
        VBox v1 = new VBox();
        v1.setSpacing(spacing);
        v1.getChildren().addAll(name, email);

        //segunda columna
        HBox h1 = new HBox();
        h1.setAlignment(Pos.CENTER);
        h1.setSpacing(spacing * 2);
        h1.getChildren().addAll(acceptBtn, declineBtn);

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

        acceptBtn.getStyleClass().add("accept-request-btn");
        acceptBtnIcon.getStyleClass().add("accept-request-icon");

        declineBtn.getStyleClass().add("remove-patient-btn");
        declineBtnIcon.getStyleClass().add("remove-patient-icon");
    }

    public Button getAcceptBtn() {
        return acceptBtn;
    }

    public Button getDeclineBtn() {
        return declineBtn;
    }

    public MonitoringRequest getRequest() {
        return r;
    }

    public String getRequestId() {
        return r.getId();
    }

    public boolean displaysRequest(MonitoringRequest r2) {
        if (r2.getId() == null || r2.getId().isEmpty()) return false;
        return r.getId().equals(r2.getId());
    }
}
