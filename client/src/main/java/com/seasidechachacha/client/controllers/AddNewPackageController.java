package com.seasidechachacha.client.controllers;

import java.io.IOException;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.database.ManagerDao;
import com.seasidechachacha.client.global.Session;
import com.seasidechachacha.client.global.TaskExecutor;
import com.seasidechachacha.client.models.Package;
import com.seasidechachacha.client.utils.Alert;
import com.seasidechachacha.client.utils.Validation;
import javafx.concurrent.Task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class AddNewPackageController {

    private static final Logger logger = LogManager.getLogger(AddNewPackageController.class);

    @FXML
    private TextField tfName, tfLimit, tfPrice, tfFirst, tfSecond, tfThird;

    @FXML
    private ComboBox cbFirst, cbSecond, cbThird;

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
        tfSecond.setVisible(false);
        cbSecond.setVisible(false);
        tfThird.setVisible(false);
        cbThird.setVisible(false);

        String[] units = {"ngày", "tuần", "tháng"};
        cbFirst.getItems().addAll(units);
        cbFirst.getSelectionModel().selectedItemProperty().addListener((opts, oldVal, newVal) -> {
            cbSecond.getItems().clear();
            if (newVal != null) {
                if (newVal.equals("tháng") || newVal.equals("tuần")) {
                    cbSecond.setVisible(true);
                    tfSecond.setVisible(true);
                    if (newVal.equals("tháng")) {
                        cbSecond.getItems().addAll("ngày", "tuần");
                    } else if (newVal.equals("tuần")) {
                        cbSecond.getItems().addAll("ngày");
                    }
                } else {
                    cbSecond.setVisible(false);
                    tfSecond.setVisible(false);
                }
            }
        });

        cbSecond.getSelectionModel().selectedItemProperty().addListener((opts, oldVal, newVal) -> {
            cbThird.getItems().clear();
            if (newVal != null) {
                if (newVal.equals("tuần")) {
                    cbThird.setVisible(true);
                    tfThird.setVisible(true);
                    cbThird.getItems().addAll("ngày");
                } else {
                    cbThird.setVisible(false);
                    tfThird.setVisible(false);
                }
            }
        });

        btnAdd.setOnAction(event -> {
            if (isValid()) {
                addNewPackageThread();
            }

        });
    }
    
    private void addNewPackageThread() {
          Task<Boolean> addNewPackageTask = new Task<Boolean>() {
            @Override
            public Boolean call() {
                Package pack = getCurrentInput();
                if (!manager.addPackage(pack)) {
                    return false;
                }
                return true;
            }
        };

        addNewPackageTask.setOnSucceeded(e -> {
            boolean result = addNewPackageTask.getValue();
            if (result) {
                Alert.showAlert(AlertType.INFORMATION, "Quản lý nhu yếu phẩm", "Thêm mới nhu yếu phẩm thành công!");
                    refreshInput();
            } else {
                Alert.showAlert(AlertType.WARNING, "Quản lý nhu yếu phẩm", "Nhu yếu phẩm đã tồn tại!");
            }
        });

        TaskExecutor.execute(addNewPackageTask);
    }

    private Package getCurrentInput() {
        Package pack = null;
        String name = tfName.getText();
        int limit = Integer.valueOf(tfLimit.getText());
        int day = getDayCooldown();
        double price = Double.valueOf(tfPrice.getText());
        pack = new Package(0, name, limit, day, price);
        return pack;
    }

    private int getDayCooldown() {
        int day = 0;
        if (tfThird.isVisible()) {
            day += Integer.valueOf(tfThird.getText());
        }
        if (tfSecond.isVisible()) {
            if (cbSecond.getValue().toString().equals("ngày")) {
                day += Integer.valueOf(tfSecond.getText());
            } else if (cbSecond.getValue().toString().equals("tuần")) {
                day += 7 * Integer.valueOf(tfSecond.getText());
            }
        }
        int first = Integer.valueOf(tfFirst.getText());
        switch (cbFirst.getValue().toString()) {
            case "tháng":
                day += 30 * first;
                break;
            case "tuần":
                day += 7 * first;
                break;
            case "ngày":
                day += first;
                break;
            default:
                break;
        }
        return day;
    }

    private boolean isValid() {
        boolean valid = true;
        if (tfName.getText().equals("") || tfLimit.getText().equals("") || (tfFirst.isVisible() && tfFirst.getText().equals(""))
                || (tfSecond.isVisible() && tfSecond.getText().equals("")) || (tfThird.isVisible() && tfThird.getText().equals(""))
                || tfPrice.getText().equals("")) {
            Alert.showAlert(AlertType.WARNING, "Thêm mới nhu yếu phẩm", "Vui lòng điền đầy đủ thông tin!");
            valid = false;
        } else if ((cbFirst.isVisible() && cbFirst.getValue() == null) || (cbSecond.isVisible() && cbSecond.getValue() == null)
                || (cbThird.isVisible() && cbThird.getValue() == null)) {
            Alert.showAlert(AlertType.WARNING, "Thêm mới nhu yếu phẩm", "Vui lòng chọn đơn vị ngày, tuần, tháng!");
            valid = false;
        } else if (!checkPackageName(tfName.getText()) || !checkPackagePrice(Integer.valueOf(tfPrice.getText()))) {
            valid = false;
        } else if (Validation.isCharacterExisted(tfLimit.getText()) || Validation.isCharacterExisted(tfFirst.getText())
                || Validation.isCharacterExisted(tfSecond.getText()) || Validation.isCharacterExisted(tfThird.getText()) || Validation.isCharacterExisted(tfPrice.getText())) {
            Alert.showAlert(AlertType.WARNING, "Thêm mới nhu yếu phẩm", "Mức giới hạn, thời gian giới hạn và đơn giá chỉ bao gồm ký tự số!");
            valid = false;
        } else if (Integer.valueOf(tfLimit.getText()) <= 0 || (tfFirst.isVisible() && Integer.valueOf(tfFirst.getText()) <= 0)
                || (tfSecond.isVisible() && Integer.valueOf(tfSecond.getText()) <= 0)
                || (tfThird.isVisible() && Integer.valueOf(tfThird.getText()) <= 0) || Integer.valueOf(tfPrice.getText()) <= 0) {
            Alert.showAlert(AlertType.WARNING, "Thêm mới nhu yếu phẩm", "Vui lòng điền số lớn hơn 0 mức giới hạn, đơn giá và thời gian giới hạn!");
            valid = false;
        }
        return valid;
    }

    private boolean checkPackageName(String name) {
        if (!Validation.isCharacterExisted(name)) {
            Alert.showAlert(AlertType.WARNING, "Thêm mới nhu yếu phẩm", "Tên gói nhu yếu phẩm phải bao gồm chữ cái!");
            return false;
        }
        return true;
    }

    private boolean checkPackagePrice(int price) {
        if (price < 10000 || price > 1000000) {
            Alert.showAlert(AlertType.WARNING, "Thêm mới nhu yếu phẩm", "Giá của nhu yếu phẩm phải nằm trong khoảng 10000 - 1000000!");
            return false;
        }
        return true;
    }

    private void refreshInput() {
        tfName.setText("");
        tfLimit.setText("");
        tfFirst.setText("");
        cbFirst.getSelectionModel().clearSelection();
        tfSecond.setVisible(false);
        cbSecond.setVisible(false);
        tfThird.setVisible(false);
        cbThird.setVisible(false);
        tfPrice.setText("");
    }

}
