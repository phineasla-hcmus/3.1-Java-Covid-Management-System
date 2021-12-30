package com.seasidechachacha.client.controllers;

import java.io.IOException;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.database.ManagerDao;
import com.seasidechachacha.client.global.Session;
import com.seasidechachacha.client.models.Package;
import com.seasidechachacha.client.utils.Alert;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.NumberStringConverter;

public class AddNewPackageController {

    private static final Logger logger = LogManager.getLogger(AddNewPackageController.class);

    @FXML
    private TextField tfName, tfLimit, tfPrice, tfDayCooldown;

    @FXML
    private Button btnAdd;

    private ManagerDao manager = new ManagerDao(Session.getUser().getUserId());

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
        btnAdd.setOnAction(event -> {
            if (isValid()) {
                if (manager.addPackage(getCurrentInput())) {
                    Alert.showAlert(AlertType.INFORMATION, "Quản lý nhu yếu phẩm", "Thêm mới nhu yếu phẩm thành công!");
                    refreshInput();
                } else {
                    Alert.showAlert(AlertType.WARNING, "Quản lý nhu yếu phẩm", "Nhu yếu phẩm đã tồn tại!");
                }
            }

        });

        tfLimit.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
    }

    private Package getCurrentInput() {
        Package pack = null;
        String name = tfName.getText();
        int limit = Integer.valueOf(tfLimit.getText());
        int day = Integer.valueOf(tfDayCooldown.getText());
        double price = Double.valueOf(tfPrice.getText());
        pack = new Package(0, name, limit, day, price);
        return pack;
    }

    private boolean isValid() {
        boolean valid = true;
        if (tfName.getText().equals("") || tfLimit.getText().equals("") || tfDayCooldown.getText().equals("") || tfPrice.getText().equals("")) {
            Alert.showAlert(AlertType.WARNING, "Thêm mới nhu yếu phẩm", "Vui lòng điền đầy đủ thông tin!");
            valid = false;
        } else if (Integer.valueOf(tfLimit.getText()) <= 0 || Integer.valueOf(tfDayCooldown.getText()) <= 0 || Integer.valueOf(tfPrice.getText()) <= 0) {
            Alert.showAlert(AlertType.WARNING, "Thêm mới nhu yếu phẩm", "Vui lòng điền số lớn hơn 0 cho các trường thông tin là số!");
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
