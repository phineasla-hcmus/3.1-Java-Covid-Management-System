package com.seasidechachacha.client.controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

public class UserPaymentController {

    @FXML
    private TableView orderHistory;

    @FXML
    private Button payButton;

    @FXML
    private Label totalCost;

    @FXML
    private void initialize() {// khởi tạo trang bằng thông tin lịch sử đặt hàng lấy được từ database thêm vô bảng + cập nhật totalCost

    }

    @FXML
    private void handleButton(ActionEvent e) {
        if (e.getSource() == payButton) { // chuyển tới hệ thống thanh toán để thanh toán

        }
    }
}
