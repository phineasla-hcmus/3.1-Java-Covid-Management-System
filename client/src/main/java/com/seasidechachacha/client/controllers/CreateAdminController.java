package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.database.UserDao;
import com.seasidechachacha.client.models.User;
import com.seasidechachacha.client.payment.PaymentService;
import com.seasidechachacha.client.payment.RespondException;
import com.seasidechachacha.common.payment.ErrorResponseType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

/**
 * Nếu hệ thống khởi tạo lần đầu, table User == empty, mở CreateAdminController
 */
public class CreateAdminController {
    private static final Logger logger = LogManager.getLogger(CreateAdminController.class);
    @FXML
    private TextField username;
    @FXML
    private TextField pass1;
    @FXML
    private TextField pass2;

    @FXML
    private void back() throws IOException { // khi người dùng ấn button "quay lại"
        App.setRoot("view/Firstlogin");
    }

    public void createAdmin(ActionEvent e) throws IOException { // khi người dùng ấn button "tiếp tục"
        if (pass1.getText().compareTo(pass2.getText()) != 0) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setContentText("Mật khẩu giữa 2 lần nhập không giống nhau.\n Xin hãy nhập lại!");
            a.show();
        } else if (username.getText().isEmpty() || pass1.getText().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setContentText("Vui lòng nhập Username và Password");
            a.show();
        } else {
            int adminRoleId = 1; // Reminder purpose
            createAdminThread(new User(username.getText(), pass1.getText(), adminRoleId));
        }
    }

    /**
     * Spawn new thread for inserting admin to database, then request payment server
     * to insert admin to payment database
     * 
     * @param user
     */
    private void createAdminThread(User user) {
        Task<Boolean> createAdminTask = new Task<Boolean>() {
            @Override
            public Boolean call() {
                boolean result = UserDao.register(user);
                if (!result)
                    return false;
                PaymentService ps = new PaymentService();
                // Admin will start at 0
                try {
                    ps.requestNewAdmin(user.getUserId());
                    // If requestNewAdmin doesn't throw anything -> succeeded
                    return true;
                } catch (RespondException e) {
                    // ErrorResponseType.ID_EXISTED
                    ErrorResponseType type = e.getType();
                    logger.error(type.name() + ": " + user.getUserId());
                    // TODO: should I rollback UserDao.register()?
                    return false;
                } catch (IOException | ClassNotFoundException e) {
                    logger.error(e);
                    return false;
                }
            }
        };

        createAdminTask.setOnSucceeded(e -> {
            boolean result = createAdminTask.getValue();
            if (result) {
                try {
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setHeaderText("Thông báo!!!");
                    a.setContentText("Khởi tạo tài khoản Admin thành công.\n Xin hãy đăng nhập lại!");
                    a.show();
                    App.setRoot("view/Login");
                } catch (IOException e1) {
                    logger.error(e1);
                }
            } else {
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setHeaderText("Thông báo!!!");
                a.setContentText("Khởi tạo tài khoản Admin không thành công.\n Xin hãy khởi tạo lại!");
                a.show();
            }
        });
    }
}
