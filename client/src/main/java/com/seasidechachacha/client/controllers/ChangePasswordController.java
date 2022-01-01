package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.global.Session;
import com.seasidechachacha.client.models.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChangePasswordController {

    private static final Logger logger = LogManager.getLogger(ChangePasswordController.class);

    @FXML
    private PasswordField pfCurrentPass, pfNewPass, pfConfirmPass;
    @FXML
    private Button btnChangePassword;
    // current user
    private User user = Session.getUser();

    @FXML
    private void initialize() {
        btnChangePassword.setOnAction(event -> {
            // TODO
        });
    }
}
