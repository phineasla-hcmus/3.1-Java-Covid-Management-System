package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.database.AccountDao;
import com.seasidechachacha.client.models.Account;

import java.io.IOException;
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
            boolean result = AccountDao.register(new Account(username.getText(), pass1.getText(), managerRoleId));
            if (result) {
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
}
