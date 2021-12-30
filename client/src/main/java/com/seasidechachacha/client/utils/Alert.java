package com.seasidechachacha.client.utils;
import javafx.scene.control.Alert.AlertType;

public class Alert {

    public static void showAlert(AlertType type, String header, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(type);
        alert.setHeight(300);
        alert.setWidth(300);
        alert.setTitle("Thông báo");
        alert.setHeaderText(header);
        alert.setContentText(message);

        alert.showAndWait();
    }
}
