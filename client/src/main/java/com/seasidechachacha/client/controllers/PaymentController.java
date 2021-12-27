package com.seasidechachacha.client.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import com.seasidechachacha.client.database.PaymentDao;
import com.seasidechachacha.client.global.Session;
import com.seasidechachacha.client.global.TaskExecutor;
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
    private Label totalLabel, balanceLabel;

    // Số tiền cần phải trả, tổng từ Cart
    private double total;
    // Số tiền hiện có trong tài khoản
    private double balance;

    @FXML
    private void initialize() { // chỗ này lấy dữ liệu giá phải trả + tiền còn trong tài khoản của người dùng
        // balanceLabel.setText("0 VND");
        getPendingPaymentTotalPriceThread(Session.getUserId());
        getBalanceThread(Session.getUserId());
    }

    @FXML
    private void handleButton(ActionEvent e) {
        if (e.getSource() == back) { // thoát payment system

        } else {
            if (e.getSource() == pay) { // đồng ý thanh toán

                Alert alert = new Alert(AlertType.CONFIRMATION); // make sure người dùng thanh toán
                alert.setTitle("Xác nhận");
                alert.setHeaderText("Hãy xác nhận thanh toán ?");
                alert.setContentText("Số dư nợ phải trả : " + totalLabel.getText() + "\n" + "Số dư nợ còn lại : "
                        + balanceLabel.getText());
                alert.show();
                // option != null.
                Optional<ButtonType> option = alert.showAndWait();

                if (option.get() == null) {

                } else if (option.get() == ButtonType.OK) { // đồng ý thanh toán , trừ vô tài khoản của người dùng
                    paymentThread(Session.getUserId(), total);
                } else if (option.get() == ButtonType.CANCEL) { // không có gì xảy ra

                }
            }
        }
    }

    private void getPendingPaymentTotalPriceThread(String userId) {
        Task<Double> getPendingPaymentTotalPriceTask = new Task<Double>() {
            @Override
            public Double call() throws SQLException {
                return PaymentDao.getPendingPaymentTotalPrice(userId);
            }
        };

        getPendingPaymentTotalPriceTask.setOnSucceeded(e -> {
            double total = getPendingPaymentTotalPriceTask.getValue();
            totalLabel.setText(total + " VND");
        });
        getPendingPaymentTotalPriceTask.setOnFailed(e -> {
            Throwable throwable = getPendingPaymentTotalPriceTask.getException();
            logger.error(throwable);
        });
        TaskExecutor.execute(getPendingPaymentTotalPriceTask);
    }

    private void getBalanceThread(String userId) {
        Task<UserResponse> getBalanceTask = new Task<UserResponse>() {
            @Override
            public UserResponse call() throws IOException, ClassNotFoundException, RespondException {
                return paymentService.requestBalance(userId);
            }
        };

        getBalanceTask.setOnSucceeded(e -> {
            // Show balance
            UserResponse res = getBalanceTask.getValue();
            balance = res.getDeposit();
            balanceLabel.setText(balance + " VND");
        });

        getBalanceTask.setOnFailed(e -> {
            // requestBalance() can throw RespondException
            Throwable throwable = getBalanceTask.getException();
            if (throwable instanceof RespondException) {
                RespondException err = (RespondException) throwable;
                // Always ErrorResponseType.ID_NOT_FOUND
                ErrorResponseType type = err.getType();
                logger.error(type.name() + ": " + userId);
            } else {
                // IOException, ClassNotFoundException
                logger.error(throwable);
            }
        });

        TaskExecutor.execute(getBalanceTask);
    }

    private void paymentThread(String userId, double amount) {
        Task<PaymentResponse> payTask = new Task<PaymentResponse>() {
            @Override
            public PaymentResponse call() throws IOException, ClassNotFoundException, RespondException {
                return paymentService.requestPayment(userId, amount);
            }
        };

        payTask.setOnSucceeded(e -> {
            Alert alert = new Alert(AlertType.INFORMATION); // make sure người dùng thanh toán
            alert.setTitle("Thông báo");
            alert.setHeaderText("Thanh toán thành công!!!");
            alert.show();
        });

        payTask.setOnFailed(e -> {
            Throwable throwable = payTask.getException();
            if (throwable instanceof RespondException) {
                // requestPayment() can throw RespondException
                RespondException err = (RespondException) throwable;
                // ErrorResponseType.INSUFFICIENT_FUNDS or ErrorResponseType.ID_NOT_FOUND
                ErrorResponseType type = err.getType();
                logger.error(type.name() + ": " + userId);
                if (type == ErrorResponseType.INSUFFICIENT_FUNDS) {
                    // TODO@leesuby alert user
                    Alert alert = new Alert(AlertType.INFORMATION); // make sure người dùng thanh toán
                    alert.setTitle("Thông báo");
                    alert.setHeaderText("Số tiền trong tài khoản không đủ!!!\n Hãy nạp tiền thêm");
                    alert.show();
                }
            } else {
                // IOException, ClassNotFoundException
                logger.error(throwable);
            }
        });
    }
}
