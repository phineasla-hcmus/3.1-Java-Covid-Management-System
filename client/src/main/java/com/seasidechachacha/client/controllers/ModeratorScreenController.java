package com.seasidechachacha.client.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import com.jfoenix.controls.JFXButton;
import com.seasidechachacha.client.App;
import javafx.scene.control.Button;
import org.apache.logging.log4j.LogManager;

public class ModeratorScreenController {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(LoginController.class);
    @FXML
    private JFXButton btn_all, btn_core, btn_xeom, btnChangePassword;

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {
        if (event.getSource() == btn_all) {
            App.setCurrentPane("pn_all", "view/ViewListUser", null);
        } else if (event.getSource() == btn_core) {
            App.setCurrentPane("pn_all", "view/ViewListPackage", null);
        } else if (event.getSource() == btn_xeom) {
            App.setCurrentPane("pn_all", "view/ModeratorStatistic", null);
        } else if (event.getSource() == btnChangePassword) {
            App.setCurrentPane("pn_all", "view/ChangePassword", null);
        }
    }

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
}
