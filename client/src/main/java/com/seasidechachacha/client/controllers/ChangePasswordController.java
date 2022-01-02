package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.database.UserDao;
import com.seasidechachacha.client.global.Session;
import com.seasidechachacha.client.global.TaskExecutor;
import com.seasidechachacha.client.utils.Alert;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

public class ChangePasswordController {

    private static final Logger logger = LogManager.getLogger(ChangePasswordController.class);

    @FXML
    private PasswordField pfCurrentPass, pfNewPass, pfConfirmPass;
    @FXML
    private Button btnChangePassword;

    @FXML
    private void initialize() {
        btnChangePassword.setOnAction(event -> {
            String currentPass = pfCurrentPass.getText();
            String newPass = pfNewPass.getText();
            String confirmPass = pfConfirmPass.getText();
            if (currentPass.isBlank()
                    || newPass.isBlank()
                    || confirmPass.isBlank()) {
                Alert.showAlert(AlertType.WARNING, "Thay đổi mật khẩu", "Vui lòng điền đầy đủ thông tin!");
            } else if (!newPass.equals(confirmPass)) {
                Alert.showAlert(AlertType.WARNING, "Thay đổi mật khẩu",
                        "Mật khẩu mới không giống với mật khẩu xác nhận!");
            } else {
                changePasswordThread(currentPass, newPass);
            }
        });
    }

    private void refreshInput() {
        pfCurrentPass.setText("");
        pfNewPass.setText("");
        pfConfirmPass.setText("");
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
            resolveChangePassword(ev, result);

        });

        TaskExecutor.execute(changePasswordTask);
    }

    private void resolveChangePassword(WorkerStateEvent ev, boolean result) {
        if (result) {
            Alert.showAlert(AlertType.INFORMATION, "Thay đổi mật khẩu", "Thay đổi mật khẩu thành công!");
            refreshInput();
        } else {
            Alert.showAlert(AlertType.WARNING, "Thay đổi mật khẩu", "Vui lòng kiểm tra lại mật khẩu hiện tại!");
        }
    }
}
