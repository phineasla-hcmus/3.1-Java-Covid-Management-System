package com.seasidechachacha.client.controllers;

import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

public class PaymentController {

    @FXML
    private Button back, pay;

    @FXML
    private Label balanceRemain, balancePay;
    
    @FXML
    private void initialize() {  // chỗ này lấy dữ liệu giá phải trả + tiền còn trong tài khoản của người dùng
        balanceRemain.setText("0 VND");
        balancePay.setText("0 VND");
        
    }

    @FXML
    private void handleButton(ActionEvent e) {
        if (e.getSource() == back) { // thoát payment system

        } 
        else {
            if (e.getSource() == pay) {  // đồng ý thanh toán 
                
                Alert alert = new Alert(AlertType.CONFIRMATION);  // make sure người dùng thanh toán
                alert.setTitle("Confirm Payment");
                alert.setHeaderText("Are you sure want pay this cost?");
                alert.setContentText("Remain balance: " + balanceRemain.getText() + "\n" + "Balance need to pay: " + balancePay.getText());

                // option != null.
                Optional<ButtonType> option = alert.showAndWait();

                if (option.get() == null) {

                } else if (option.get() == ButtonType.OK) { // đồng ý thanh toán , trừ vô tài khoản của người dùng

                } else if (option.get() == ButtonType.CANCEL) { // không có gì xảy ra

                }
            }
        }
    }
}
