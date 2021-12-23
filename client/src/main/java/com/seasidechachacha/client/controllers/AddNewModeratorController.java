package com.seasidechachacha.client.controllers;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.seasidechachacha.client.database.UserDao;
import com.seasidechachacha.client.models.User;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class AddNewModeratorController {
    @FXML
    private TextField username;
    @FXML
    private TextField pass1;
    @FXML
    private TextField pass2;

    private Executor exec;

    public AddNewModeratorController() {
        exec = Executors.newFixedThreadPool(1);
    }

    public void createModerator(ActionEvent e) throws IOException {
        if (pass1.getText().compareTo(pass2.getText()) != 0) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setContentText("Mật khẩu giữa 2 lần nhập không giống nhau.\n Xin hãy nhập lại!");
            a.show();
        } else if (username.getText().isEmpty() || pass1.getText().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setContentText("Vui lòng nhập Username và Password");
            a.show();
        } else {
            int managerRoleId = 2; // Reminder purpose
            addModeratorThread(new User(username.getText(), pass1.getText(), managerRoleId));
        }
    }

    /**
     * Thread wrapper to call UserDao
     *
     */
    private void addModeratorThread(User mod) {
        Task<Boolean> addModeratorTask = new Task<Boolean>() {
            @Override
            public Boolean call() {
                return UserDao.register(mod);
            }
        };
        addModeratorTask.setOnSucceeded(e -> {
            resolveAddModerator(e, addModeratorTask.getValue());
        });
        exec.execute(addModeratorTask);
    }

    private void resolveAddModerator(WorkerStateEvent e, boolean status) {
        if (status) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("Thêm mới người quản lý thành công!!!");
            a.show();
        } else {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setContentText("Thêm mới người quản lý không thành công!!!");
            a.show();
        }
    }
}
