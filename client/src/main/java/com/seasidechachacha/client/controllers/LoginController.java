package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.database.UserDao;
import com.seasidechachacha.client.models.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
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

    // Set in resolveLogin
    private User user = null;

    // Provide thread to CRUD database
    private Executor exec;

    @FXML
    private void initialize() {
        // https://stackoverflow.com/questions/30249493/using-threads-to-make-database-requests
        exec = Executors.newFixedThreadPool(1);

        next.setOnAction(e -> {
            String userId = username.getText();
            String password = pass.getText();
            if (userId.isEmpty() || password.isEmpty()) {
                loginThread(userId, password);
            }
            // nếu đăng nhập thành công và database xác nhận là user này mới đăng nhập
            // lần đầu

            // // là moderator
            // App.setRoot("view/ModeratorScreen");
            // // App.initializeMainScreen();

            // // tạm thời comment để xử lý chia role
            // // if (true) {
            // // App.setRoot("view/CreateUserPassword");
            // // } // nếu đăng nhập thành công và role là moderator
            // // else {
            // // App.initializeMainScreen();
            // // }
        });
    }

    /**
     * Create and execute database request for login, which then will be forward to
     * resolveLogin
     * 
     * @param userId
     * @param password
     */
    private void loginThread(String userId, String password) {
        Task<User> loginTask = new Task<User>() {
            @Override
            public User call() {
                return UserDao.login(userId, password);
            }
        };
        loginTask.setOnSucceeded(e -> {
            resolveLogin(e, loginTask.getValue());
        });
        exec.execute(loginTask);
    }

    /**
     * Create and execute database request for checking new user, which then will be
     * forward to resolveIsNewUser
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
            resolveIsNewUser(e, isNewUserTask.getValue());
        });
        exec.execute(isNewUserTask);
    }

    public void resolveLogin(WorkerStateEvent e, User user) {
        if (user == null) {
            // TODO No user found, pop some Alert to the UI
        }
        this.user = user;
        int roleId = user.getRoleId();
        if (roleId == 1) {
            // TODO Admin
        } else if (roleId == 2) {
            // TODO Manager
        } else if (roleId == 3) {
            isNewUserThread(user.getUserId());
        }
    }

    public void resolveIsNewUser(WorkerStateEvent e, boolean isNewUser) {
        if (isNewUser) {
            // TODO
        } else {
            // TODO
        }
    }
}
