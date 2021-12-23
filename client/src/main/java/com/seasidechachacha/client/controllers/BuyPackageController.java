package com.seasidechachacha.client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class BuyPackageController {
    @FXML
    private Label totalCost;

    @FXML
    private Button searchButton, acceptButton;

    @FXML
    private TableView packageTable;

    @FXML
    private ChoiceBox choosePackage;

    @FXML
    private TextField searchText, quantity;

    @FXML
    void handleButton(ActionEvent e) {
        if (e.getSource() == "searchButton") // khi người dùng bấm search , dựa vào searchText để tìm tên trong database
        { // sau đó xuất lên bảng
            String namePackage = searchText.getText();
        } else if (e.getSource() == acceptButton) { // TODO Phineas lưu dữ liệu từ choosePackage(Gói packet chọn) và quantity về database Cart
            
        }
    }

    @FXML
    void handleQuantity(ActionEvent e) { // khi người dùng nhập số lượng , dựa vào gói đã chọn + với đơn giá tính lại
                                         // tổng
        int q = Integer.parseInt(quantity.getText());
        float total = q * 0;

        totalCost.setText(Float.toString(total));
    }

}
