package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.database.ManagerDao;
import com.seasidechachacha.client.models.Package;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class AddNewPackageController {

    private static final Logger logger = LogManager.getLogger(AddNewPackageController.class);

    @FXML
    private TextField tfName, tfLimit, tfPrice, tfDayCooldown;

    @FXML
    private Button btnAdd;

//    @FXML
//    private ComboBox cbDayCooldown;
    @FXML
    private void goBack() {
        try {
            App.setCurrentPane("pn_all", "view/ViewListPackage", null);
        } catch (IOException ex) {
            logger.fatal(ex);
        }
    }

    @FXML
    public void initialize() {
        ManagerDao Tam = new ManagerDao("mod-19127268");
        btnAdd.setOnAction(event -> {
            if (isValid()) {
                if (Tam.addPackage(getCurrentInput())) {
                    // TODO
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Thông báo");
                    alert.setHeaderText("Quản lý nhu yếu phẩm");
                    alert.setContentText("Thêm mới nhu yếu phẩm thành công!");
                    alert.showAndWait();
                    refreshInput();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Thông báo");
                    alert.setHeaderText("Quản lý nhu yếu phẩm");
                    alert.setContentText("Nhu yếu phẩm đã tồn tại!");

                    alert.showAndWait();
                }
            }

        });
    }

    private Package getCurrentInput() {
        Package pack = null;
        String ID = "14";
        String name = tfName.getText();
        int limit = Integer.valueOf(tfLimit.getText());
        System.out.println(limit);
        int day = Integer.valueOf(tfDayCooldown.getText());
        double price = Double.valueOf(tfPrice.getText());
        pack = new Package(0, name, limit, day, price);
        return pack;
    }

    private boolean isValid() {
        boolean valid = true;
        if (tfName.getText().equals("") || tfLimit.getText().equals("") || tfDayCooldown.getText().equals("") || tfPrice.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Thông báo");
            alert.setHeaderText("Thêm mới nhu yếu phẩm");
            alert.setContentText("Vui lòng điền đầy đủ thông tin!");

            alert.showAndWait();
            valid = false;
        } else if (Integer.valueOf(tfLimit.getText()) <= 0 || Integer.valueOf(tfDayCooldown.getText()) <= 0 || Integer.valueOf(tfPrice.getText()) <= 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Thông báo");
            alert.setHeaderText("Thêm mới nhu yếu phẩm");
            alert.setContentText("Vui lòng điền số lớn hơn 0 cho các trường thông tin là số!");

            alert.showAndWait();
            valid = false;
        }
        return valid;
    }

    private void refreshInput() {
        tfName.setText("");
        tfLimit.setText("");
        tfDayCooldown.setText("");
        tfPrice.setText("");
    }

}
