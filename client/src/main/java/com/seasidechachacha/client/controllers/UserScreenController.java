package com.seasidechachacha.client.controllers;

import java.io.IOException;
import java.net.URL;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import com.jfoenix.controls.JFXButton;
import com.seasidechachacha.client.App;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserScreenController {

    private static final Logger logger = LogManager.getLogger(LoginController.class);
    @FXML
    private ScrollPane pn_all;

    @FXML
    private JFXButton btnInfo, btnPackage, btnPayment;
    
    @FXML
    private Button btnLogout;

    @FXML
    private void initialize() {
        btnLogout.setOnAction(event -> {
            try {
                App.setRoot("view/Login");
            } catch (IOException ex) {
                logger.fatal(ex);
            }
        });
    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {
        if (event.getSource() == btnInfo) {
            App.setCurrentPane("pn_all", "view/UserInfo", null);
        } else if (event.getSource() == btnPackage) {
            App.setCurrentPane("pn_all", "view/BuyPackage", null);
        } else if (event.getSource() == btnPayment) {
            App.setCurrentPane("pn_all", "view/UserPayment", null);
        }
    }

    @FXML
    private BorderPane mainBorderPane;

    private void loadFXML(URL url) {
        try {
            FXMLLoader loader = new FXMLLoader(url);
            mainBorderPane.setCenter(loader.load());
        } catch (IOException e) {
        }
    }
}
