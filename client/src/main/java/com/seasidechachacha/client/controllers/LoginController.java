package com.seasidechachacha.client.controllers;

import java.io.IOException;
import java.sql.SQLException;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.database.AdminDao;
import com.seasidechachacha.client.database.UserDao;
import com.seasidechachacha.client.global.Session;
import com.seasidechachacha.client.global.TaskExecutor;
import com.seasidechachacha.client.models.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
        next.setOnAction(e -> {
            String userId = username.getText();
            String password = pass.getText();
            if (!userId.isEmpty() && !password.isEmpty()) {
                loginThread(userId, password);
            }
        });
    }

    /**
     * Create and execute database request for login, which then will be forward
     * to resolveLogin
     *
     * @param userId
     * @param password
     */
    private void loginThread(String userId, String password) {
        Task<User> loginTask = new Task<User>() {
            @Override
            public User call() {
                return UserDao.authenticate(userId, password);
            }
        };
        loginTask.setOnSucceeded(e -> {
            try {
                resolveLogin(e, loginTask.getValue());
            } catch (IOException ex) {
                logger.error(ex);
            }
        });
        TaskExecutor.execute(loginTask);
    }

    public void resolveLogin(WorkerStateEvent e, User user) throws IOException {
        if (user == null) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setContentText("Xin kiểm tra lại tên đăng nhập hoặc mật khẩu!");
            a.show();
            return;
        }
        Session.setUser(user);
        int roleId = user.getRoleId();
        if (roleId == 1) {
            App.setRoot("view/AdminScreen");
        } else if (roleId == 2) {
            isBannedThread(user.getUserId());
        } else if (roleId == 3) {
            isNewUserThread(user.getUserId());
        }
    }

    private void isBannedThread(String managerId) {
        Task<Boolean> isBannedTask = new Task<Boolean>() {
            @Override
            public Boolean call() throws SQLException {
                return AdminDao.isBanned(managerId);
            }
        };

        isBannedTask.setOnSucceeded(e -> {
            try {
                resolveIsBanned(e, isBannedTask.getValue());
            } catch (IOException ex) {
                logger.error(ex);
            }
        });
        isBannedTask.setOnFailed(e -> {
            Throwable throwable = isBannedTask.getException();
            logger.error(throwable);
        });
        TaskExecutor.execute(isBannedTask);
    }

    private void resolveIsBanned(WorkerStateEvent e, boolean isBanned) throws IOException {
        if (isBanned) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setHeaderText("Thông báo");
            a.setContentText("Tài khoản của bạn đã bị khóa!!!\n Xin hãy liên hệ Admin để biết thêm chi tiết");
            a.show();
        } else {
            App.setRoot("view/ModeratorScreen");
        }
    }

    /**
     * Create and execute database request for checking new user, which then
     * will be forwarded to resolveIsNewUser
     *
     * @param userId
     * @param password
     */
    private void isNewUserThread(String userId) {
        Task<Boolean> isNewUserTask = new Task<Boolean>() {
            @Override
            public Boolean call() throws SQLException {
                return UserDao.isFirstLogin(userId);
            }
        };
        isNewUserTask.setOnSucceeded(e -> {
            try {
                resolveIsNewUser(e, isNewUserTask.getValue());
            } catch (IOException ex) {
                logger.error(ex);
            }
        });
        TaskExecutor.execute(isNewUserTask);
    }

    public void resolveIsNewUser(WorkerStateEvent e, boolean isNewUser) throws IOException {
        if (isNewUser) {
            App.setRoot("view/CreateUserPassword");
        } else {
            App.setRoot("view/UserScreen");
        }
    }
}
