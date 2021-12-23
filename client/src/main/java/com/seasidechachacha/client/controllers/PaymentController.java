package com.seasidechachacha.client.controllers;

import java.io.IOException;
import java.util.Optional;

import com.seasidechachacha.client.payment.PaymentService;
import com.seasidechachacha.client.payment.RespondException;
import com.seasidechachacha.common.payment.ErrorResponseType;
import com.seasidechachacha.common.payment.PaymentResponse;
import com.seasidechachacha.common.payment.UserResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

public class PaymentController {
    private static final Logger logger = LogManager.getLogger(PaymentController.class);
    private PaymentService paymentService = new PaymentService();

    @FXML
    private Button back, pay;

    @FXML
    private Label balanceRemain, balancePay;

    @FXML
    private void initialize() { // chỗ này lấy dữ liệu giá phải trả + tiền còn trong tài khoản của người dùng
        balanceRemain.setText("0 VND");
        balancePay.setText("0 VND");
        // TODO: get userId
        getBalanceThread("GET_USER_ID");
    }

    @FXML
    private void handleButton(ActionEvent e) {
        if (e.getSource() == back) { // thoát payment system

        } else {
            if (e.getSource() == pay) { // đồng ý thanh toán

                Alert alert = new Alert(AlertType.CONFIRMATION); // make sure người dùng thanh toán
                alert.setTitle("Confirm Payment");
                alert.setHeaderText("Are you sure want pay this cost?");
                alert.setContentText("Remain balance: " + balanceRemain.getText() + "\n" + "Balance need to pay: "
                        + balancePay.getText());

                // option != null.
                Optional<ButtonType> option = alert.showAndWait();

                if (option.get() == null) {

                } else if (option.get() == ButtonType.OK) { // đồng ý thanh toán , trừ vô tài khoản của người dùng
                    // TODO: get userId
                    paymentThread("GET_USER_ID", Double.parseDouble(balancePay.getText()));
                } else if (option.get() == ButtonType.CANCEL) { // không có gì xảy ra

                }
            }
        }
    }

    private void getBalanceThread(String userId) {
        Task<UserResponse> getBalanceTask = new Task<UserResponse>() {
            @Override
            public UserResponse call() throws IOException, ClassNotFoundException, RespondException {
                return paymentService.requestBalance(userId);
            }
        };

        getBalanceTask.setOnSucceeded(e -> {
            // TODO: show balance
            UserResponse res = getBalanceTask.getValue();
            res.getDeposit();
            res.getUserId();
        });

        getBalanceTask.setOnFailed(e -> {
            Throwable throwable = getBalanceTask.getException();
            if (throwable instanceof RespondException) {
                RespondException err = (RespondException) throwable;
                // TODO: getAccountBalance will return ErrorResponseType.ID_NOT_FOUND
                ErrorResponseType type = err.getType();
            } else {
                // IOException, ClassNotFoundException
                logger.error(throwable);
            }
        });
    }

    private void paymentThread(String userId, double amount) {
        Task<PaymentResponse> payTask = new Task<PaymentResponse>() {
            @Override
            public PaymentResponse call() throws IOException, ClassNotFoundException, RespondException {
                return paymentService.requestPayment(userId, amount);
            }
        };

        payTask.setOnSucceeded(e -> {
            // TODO: success
            // Save to payment history for both user and admin
        });

        payTask.setOnFailed(e -> {
            Throwable throwable = payTask.getException();
            if (throwable instanceof RespondException) {
                RespondException err = (RespondException) throwable;
                // TODO: getAccountBalance will return ErrorResponseType.INSUFFICIENT_FUNDS and ErrorResponseType.ID_NOT_FOUND
                ErrorResponseType type = err.getType();
            } else {
                // IOException, ClassNotFoundException
                logger.error(throwable);
            }
        });
    }
}
