package com.seasidechachacha.client.controllers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

public class UserPaymentController {

    @FXML
    // TODO@leesuby References to generic type TableView<S> should be parameterized
    // https://stackoverflow.com/questions/53751455/how-to-create-a-javafx-tableview-without-warnings
    private TableView orderHistory;

    @FXML
    private Button payButton;

    @FXML
    private Label totalCost;

    @FXML
    private void initialize() {// TODO Phineas lấy dữ liệu từ bảng orderhistory

    }

    @FXML
    private void handleButton(ActionEvent e) throws IOException {
        if (e.getSource() == payButton) { // chuyển tới hệ thống thanh toán để thanh toán

        }
    }
}
