package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField username;

    @FXML
    private TextField pass;

    @FXML
    private Button next;

    @FXML
    private void initialize() {
        next.setOnAction(event -> {
            try {
                // nếu đăng nhập thành công và database xác nhận là user này mới đăng nhập lần đầu
                if(true)
                    App.setRoot("view/CreateUserPassword");
                
                // nếu đăng nhập thành công và role là moderator
                else
                    App.initializeMainScreen();
            } catch (IOException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    public void login(ActionEvent e) { // có database thì xét và dựa vào role để đưa vào menu
        String user = username.getText();
        String pas = pass.getText();

    }
}
