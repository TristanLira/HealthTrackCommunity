package com.example.healthtrackcommunity.controls;

import com.example.healthtrackcommunity.models.Comment;
import com.example.healthtrackcommunity.models.Patient;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.format.DateTimeFormatter;

public class CommentDisplay extends HBox {

    private final int spacing = 5;

    private Comment comment;
    private Patient patient;

    private Label titleLabel;
    private FontIcon commentIcon;
    private Label contentLabel;

    private Label dateLabel;
    private Label patientLabel;
    private Label fromAlertLabel;

    // constructor normal
    public CommentDisplay(Comment comment) {
        super();

        this.comment = comment;
        this.patient = null;

        build();
        addCss();
    }

    // constructor con paciente
    public CommentDisplay(Comment comment, Patient patient) {
        super();

        this.comment = comment;
        this.patient = patient;

        build();
        addCss();
    }

    private void build() {

        titleLabel = new Label(comment.getTitle());
        contentLabel = new Label(comment.getContent());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = comment.getTimeObj().format(formatter);

        dateLabel = new Label(comment.getDate() + " a las " + formattedTime);

        contentLabel.setWrapText(true);

        if (patient != null) {
            patientLabel = new Label("Enviado a: " + patient.getName() + " (" + patient.getEmail() + ")");
        }

        if (comment.isFromAlert()) {
            fromAlertLabel = new Label("(Creado desde alerta médica)");
        }

        // columna izquierda
        VBox infoBox = new VBox();
        infoBox.setSpacing(spacing);


        HBox h1 = new HBox(spacing);
        h1.setAlignment(Pos.CENTER_LEFT);
        infoBox.getChildren().add(h1);

        commentIcon = new FontIcon("fas-comment");

        if (fromAlertLabel != null) {
            h1.getChildren().addAll(commentIcon, titleLabel, fromAlertLabel);
        } else {
            h1.getChildren().addAll(commentIcon, titleLabel);
        }

        infoBox.getChildren().add(contentLabel);
        infoBox.getChildren().add(dateLabel);

        if (patientLabel != null) {
            infoBox.getChildren().add(patientLabel);
        }

        // espacio flexible
        Region spacing1 = new Region();
        HBox.setHgrow(spacing1, Priority.ALWAYS);

        this.setAlignment(Pos.CENTER_LEFT);
        this.getChildren().addAll(infoBox);
    }

    private void addCss() {
        this.getStyleClass().add("comment-display");

        if (comment.isFromAlert()) {
            this.getStyleClass().add("comment-display-alert");
            fromAlertLabel.getStyleClass().add("comment-subtitle");
        }

        titleLabel.getStyleClass().add("comment-title");
        contentLabel.getStyleClass().add("comment-content");
        dateLabel.getStyleClass().add("comment-meta");

        commentIcon.getStyleClass().add("comment-icon");

        if (patientLabel != null) patientLabel.getStyleClass().add("comment-patient");
    }

    public boolean displaysComment(Comment c) {
        if (c.getId() == null || c.getId().isEmpty()) {
            return false;
        }

        return comment.getId().equals(c.getId());
    }

    public String getCommentId() {
        return comment.getId();
    }

    public Comment getComment() {
        return comment;
    }
}
