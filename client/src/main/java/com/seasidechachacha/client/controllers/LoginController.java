package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class LoginController {

    private static final Logger logger = LogManager.getLogger(LoginController.class);

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
                // nếu đăng nhập thành công và database xác nhận là user này mới đăng nhập lần
                // đầu
                
                // là moderator
                App.setRoot("view/ModeratorScreen");
//                App.initializeMainScreen();
                
                // tạm thời comment để xử lý chia role
//                if (true) {
//                    App.setRoot("view/CreateUserPassword");
//                } // nếu đăng nhập thành công và role là moderator
//                else {
//                    App.initializeMainScreen();
//                }
            } catch (IOException ex) {
                logger.fatal(ex);
            }
        });
    }

    public void login(ActionEvent e) { // có database thì xét và dựa vào role để đưa vào menu
        String user = username.getText();
        String pas = pass.getText();
    }
}
