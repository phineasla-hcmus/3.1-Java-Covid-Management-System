package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.database.UserDao;
import com.seasidechachacha.client.global.Session;
import com.seasidechachacha.client.global.TaskExecutor;
import com.seasidechachacha.client.models.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

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
            String currentPass = pfCurrentPass.getText();
            String newPass = pfNewPass.getText();
            String confirmPass = pfConfirmPass.getText();
            if (!currentPass.isBlank()
                    && !newPass.isBlank()
                    && !confirmPass.isBlank()
                    && newPass == confirmPass) {
                changePasswordThread(currentPass, newPass);
            }
        });
    }

    private void changePasswordThread(String currentPass, String newPass) {
        Task<Boolean> changePasswordTask = new Task<Boolean>() {
            @Override
            public Boolean call() {
                return UserDao.changePassword(Session.getUserId(), currentPass, newPass);
            }
        };

        changePasswordTask.setOnSucceeded(ev -> {
            Boolean result = changePasswordTask.getValue();
            if (result == null) {
                // How did we get here
                logger.error("Bug in DAO changePassword");
            } else {
                resolveChangePassword(ev, result);
            }
        });

        TaskExecutor.execute(changePasswordTask);
    }

    private void resolveChangePassword(WorkerStateEvent ev, boolean result) {
        if (result) {
            // Success
        } else {
            // Wrong oldPassword
        }
    }
}
