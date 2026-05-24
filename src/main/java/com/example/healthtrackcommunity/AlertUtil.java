package com.example.healthtrackcommunity;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class AlertUtil {

    public static void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(title);
        alert.setContentText(message);
        //addAlertCss(alert, Alert.AlertType.INFORMATION);
        addAlertCss(alert);
        alert.show();
    }

    public static void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        addAlertCss(alert, Alert.AlertType.ERROR);
        alert.show();
    }

    public static void showConfirmationAlert(String title, String message, Runnable onConfirm) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText(title);
        alert.setContentText(message);
        addAlertCss(alert, Alert.AlertType.CONFIRMATION);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            onConfirm.run();
        } else {
            alert.close();
        }
    }

    public static void addAlertCss(Alert alert, Alert.AlertType type) {
        //alert.initStyle(StageStyle.UNDECORATED);
        alert.getDialogPane().getStylesheets().add(AlertUtil.class
                .getResource("/com/example/healthtrackcommunity/css/alerts.css").toExternalForm());

        switch (type) {
            case INFORMATION -> alert.getDialogPane().getStyleClass().add("info-alert");

            case CONFIRMATION -> alert.getDialogPane().getStyleClass().add("confirm-alert");

            case WARNING -> alert.getDialogPane().getStyleClass().add("warning-alert");

            case ERROR -> alert.getDialogPane().getStyleClass().add("error-alert");

            default -> alert.getDialogPane().getStyleClass().add("default-alert");
        }
    }

    public static void addAlertCss(Alert alert) {
        alert.getDialogPane().getStylesheets().add(AlertUtil.class
                .getResource("/com/example/healthtrackcommunity/css/alerts.css").toExternalForm());

        alert.getDialogPane().getStyleClass().add("default-alert");
    }
}
