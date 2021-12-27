package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.database.UserDao;
import com.seasidechachacha.client.global.Session;
import com.seasidechachacha.client.global.TaskExecutor;

import java.io.IOException;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class CreateUserPasswordController {
    @FXML
    private TextField pass1;
    @FXML
    private TextField pass2;

    @FXML
    private void back() throws IOException { // khi người dùng ấn button "quay lại"
        App.setRoot("view/Login");
    }

    public void createPassword(ActionEvent e) throws IOException { // khi người dùng ấn button "tiếp tục"
        if (pass1.getText().compareTo(pass2.getText()) != 0) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setContentText("Mật khẩu giữa 2 lần nhập không giống nhau.\n Xin hãy nhập lại!");
            a.show();
        } else {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("Khởi tạo mật khẩu thành công!!!");
            a.show();
            // khởi tạo thành công, cập nhật database và cho vô screen User
            changePasswordThread(Session.getUser().getUserId(), pass1.getText());
        }
    }

    private void changePasswordThread(String userId, String password) {
        Task<Boolean> changePasswordTask = new Task<Boolean>() {
            @Override
            public Boolean call() {
                return UserDao.changePassword(userId, password);
            }
        };
        changePasswordTask.setOnSucceeded(e -> {
            try {
                resolveChangePassword(e, changePasswordTask.getValue());
            } catch (IOException ex) {
                // Error in App#initializeMainScreen()
            }
        });
        TaskExecutor.execute(changePasswordTask);
    }

    private void resolveChangePassword(WorkerStateEvent e, boolean result) throws IOException {
        // TODO@leesuby
        if (result)
            App.initializeMainScreen(); // này là screen moderator :))
        else {
            // Something went wrong in the database
        }
    }

    private void removeFirstLoginThread(String userId) {
        Task<Boolean> removeFirstLoginTask = new Task<Boolean>() {
            @Override
            public Boolean call() {
                return UserDao.removeFirstLogin(userId);
            }
        };
        removeFirstLoginTask.setOnSucceeded(e -> {
            try {
                resolveRemoveFirstLogin(e, removeFirstLoginTask.getValue());
            } catch (IOException ex) {
                // Error in App#initializeMainScreen()
            }
        });
        TaskExecutor.execute(removeFirstLoginTask);
    }

    private void resolveRemoveFirstLogin(WorkerStateEvent e, boolean result) {
        if (result) {

        } else {

        }
    }
}
